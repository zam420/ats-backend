package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.ApiResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewExtController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    private static Map<String, Object> vn(int v, String n) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("value", v);
        m.put("name", n);
        return m;
    }

    private static String roundTypeName(int t) {
        return switch (t) {
            case 1 -> "HR_ROUND";
            case 2 -> "TECHNICAL_ROUND";
            case 3 -> "FUNCTIONAL_ROUND";
            case 4 -> "MANAGER_ROUND";
            default -> "ROUND_" + t;
        };
    }

    private static String resumeStatusName(int s) {
        return switch (s) {
            case -1 -> "TALENT_POOL";
            case 0 -> "NEW";
            default -> "STATUS_" + s;
        };
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/forMe")
    public ApiResult<Object> forMe(@RequestParam(value = "time_type", required = false) Integer timeType) {
        Long uid = AuthService.getLoggedInUserId();
        String sql = "SELECT air.id round_id, air.round_type, air.modified_by_id modified_by, air.is_online, "
                + "air.from_time, air.to_time, air.interviewers, air.status round_status, "
                + "aic.id interview_candidate_id, aic.status interview_candidate_status, "
                + "acr.id candidate_resume_id, acr.name candidate_name, acr.status resume_status, "
                + "ai.target_job_id, aif.id feedback_id "
                + "FROM ats_interview_candidate aic "
                + "JOIN ats_interview_rounds air ON aic.round_id = air.id "
                + "JOIN ats_interview_record ai ON ai.id = air.interview_id "
                + "JOIN ats_candidate_resume acr ON aic.candidate_resume_id = acr.id "
                + "LEFT JOIN ats_interview_feedback aif ON air.id = aif.interview_round_id "
                + "AND aic.id = aif.interview_candidate_id "
                + "WHERE air.status > 0 AND aic.status >= 0 AND (air.modified_by_id = ? "
                + "OR JSON_CONTAINS(air.interviewers, JSON_OBJECT('id', ?)) "
                + "OR JSON_CONTAINS(air.interviewers, CAST(? AS JSON)))";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, uid, uid, String.valueOf(uid));
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        int tt = timeType == null ? 0 : timeType;
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            LocalDateTime from = toLdt(r.get("from_time"));
            LocalDateTime to = toLdt(r.get("to_time"));
            if (tt == 1) {
                if (from == null || !from.isAfter(now)) continue;
            } else if (tt == 2) {
                if (to == null || !to.isBefore(now)) continue;
            } else {
                if (from == null || !from.toLocalDate().equals(today)) continue;
            }
            out.add(buildRow(r, from, to, now, uid));
        }
        if (out.isEmpty()) return ApiResult.success(null);
        if (tt == 2) {
            out.sort((a, b) -> String.valueOf(b.get("interview_time_to")).compareTo(String.valueOf(a.get("interview_time_to"))));
        } else {
            out.sort(Comparator.comparing(a -> String.valueOf(a.get("interview_time_from"))));
        }
        return ApiResult.success(out);
    }

    private Map<String, Object> buildRow(Map<String, Object> r, LocalDateTime from, LocalDateTime to,
                                         LocalDateTime now, Long uid) {
        Map<String, Object> m = new LinkedHashMap<>();
        int roundStatus = ((Number) r.get("round_status")).intValue();
        int icStatus = ((Number) r.get("interview_candidate_status")).intValue();
        Object interviewers = parseJson(r.get("interviewers"));
        m.put("round_id", r.get("round_id"));
        m.put("is_online", r.get("is_online") != null && toBool(r.get("is_online")));
        m.put("interview_time_from", from == null ? null : from.format(FMT));
        m.put("interview_time_to", to == null ? null : to.format(FMT));
        m.put("interviewers", interviewers);
        m.put("interview_candidate_id", r.get("interview_candidate_id"));
        m.put("candidate_resume_id", r.get("candidate_resume_id"));
        m.put("candidate_name", r.get("candidate_name"));
        Object jobId = r.get("target_job_id");
        m.put("applying_position_id", jobId);
        String title = null;
        List<Object> locs = new ArrayList<>();
        if (jobId != null) {
            List<Map<String, Object>> j = jdbc.queryForList("SELECT title, work_location FROM ats_job_position WHERE id = ?", jobId);
            if (!j.isEmpty()) {
                title = (String) j.get(0).get("title");
                Object wl = parseJson(j.get(0).get("work_location"));
                if (wl instanceof List<?> l) {
                    for (Object o : l) {
                        int v = ((Number) o).intValue();
                        locs.add(vn(v, "LOCATION_" + v));
                    }
                }
            }
        }
        m.put("applying_position_title", title);
        m.put("applying_position_locations", locs);
        m.put("feedback_id", r.get("feedback_id"));
        Object modifier = r.get("modified_by");
        m.put("recruiter_id", modifier);
        String recruiter = null;
        if (modifier != null) {
            List<String> n = jdbc.queryForList("SELECT name FROM ats_user WHERE id = ?", String.class, modifier);
            if (!n.isEmpty()) recruiter = n.get(0);
        }
        m.put("recruiter_name", recruiter);
        m.put("round_last_modifier", modifier);
        if (icStatus > 0) m.put("status", vn(2, "Evaluated"));
        else if (roundStatus == 1) m.put("status", vn(0, "Awaiting_Interview"));
        else m.put("status", vn(1, "Awaiting_Feedback"));
        int rt = ((Number) r.get("round_type")).intValue();
        m.put("round_type", vn(rt, roundTypeName(rt)));
        int rs = r.get("resume_status") == null ? 0 : ((Number) r.get("resume_status")).intValue();
        m.put("candidate_resume_status", vn(rs, resumeStatusName(rs)));
        boolean involved = modifier != null && uid != null && ((Number) modifier).longValue() == uid;
        if (!involved && interviewers instanceof List<?> l) {
            for (Object o : l) {
                Object id = o instanceof Map<?, ?> mm ? mm.get("id") : o;
                if (id instanceof Number nn && uid != null && nn.longValue() == uid) involved = true;
            }
        }
        boolean editable = roundStatus > 0 && from != null && !now.isBefore(from) && involved;
        m.put("read_only", !editable);
        return m;
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/submitInBulk")
    @Transactional
    public ApiResult<Object> submitInBulk(@RequestBody Map<String, Object> body) {
        Long uid = AuthService.getLoggedInUserId();
        JsonNode n = mapper.valueToTree(body);
        long targetJob = n.path("target_job").asLong();
        JsonNode candidates = n.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) throw new InternalServerException("candidates can not be empty");
        for (JsonNode c : candidates) {
            Integer cnt = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM ats_candidate_resume WHERE id = ? AND applying_position_id = ?",
                    Integer.class, c.path("candidate_resume_id").asLong(), targetJob);
            if (cnt == null || cnt == 0) throw new InternalServerException("Resume does not belong to the target job");
        }
        String interviewers = n.path("interviewers").toString();
        Timestamp from = parseTs(n.path("time_from").asText(null));
        Timestamp to = parseTs(n.path("time_to").asText(null));
        Long tmpl = n.hasNonNull("interview_template") ? n.get("interview_template").asLong() : null;
        Long emailTmpl = n.hasNonNull("email_template") ? n.get("email_template").asLong() : null;
        List<Long> ids = new ArrayList<>();
        for (JsonNode c : candidates) {
            long interviewId = insert(
                    "INSERT INTO ats_interview_record (target_job_id, created_by_id, modified_by_id, interview_type, "
                            + "interview_template_id, interview_status, created_at, updated_at) VALUES (?,?,?,?,?,?,NOW(6),NOW(6))",
                    targetJob, uid, uid, 1, tmpl, 0);
            long roundId = insert(
                    "INSERT INTO ats_interview_rounds (interview_id, round_code, `order`, round_type, interviewers, from_time, "
                            + "to_time, is_online, interview_location, info_interviewer, info_candidate, email_template_id, status, "
                            + "created_at, updated_at, created_by_id, modified_by_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(6),NOW(6),?,?)",
                    interviewId, "ROUND_1", 1, 3, interviewers, from, to, n.path("is_online").asBoolean(false),
                    n.path("interview_location").asText(null), n.path("info_interviewer").asBoolean(false),
                    n.path("info_candidate").asBoolean(false), emailTmpl, 1, uid, uid);
            jdbc.update("INSERT INTO ats_interview_candidate (round_id, candidate_resume_id, status, created_at, updated_at) "
                    + "VALUES (?,?,0,NOW(6),NOW(6))", roundId, c.path("candidate_resume_id").asLong());
            ids.add(interviewId);
        }
        return ApiResult.success(ids);
    }

    @PreAuthorize("hasAnyRole('sa','proa')")
    @PostMapping("/processTemp/share")
    @Transactional
    public ApiResult<Object> shareProcessTemplate(@RequestBody Map<String, Object> body) {
        Long uid = AuthService.getLoggedInUserId();
        JsonNode n = mapper.valueToTree(body);
        long iptId = n.path("ipt_id").asLong();
        List<Long> users = new ArrayList<>();
        n.path("share_to").forEach(x -> users.add(x.asLong()));
        if (users.contains(uid)) throw new InternalServerException("Can not share to yourself");
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM ats_interview_process_template WHERE id = ? AND status = 0",
                Integer.class, iptId);
        if (cnt == null || cnt == 0) throw new InternalServerException("Interview process template not found");
        for (Long u : users) {
            Integer ok = jdbc.queryForObject("SELECT COUNT(*) FROM ats_user WHERE id = ? AND status = 0", Integer.class, u);
            if (ok == null || ok == 0) throw new InternalServerException("User not found: " + u);
            Integer ex = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM ats_interview_process_template_share_to WHERE interviewprocesstemplate_id = ? AND user_id = ?",
                    Integer.class, iptId, u);
            if (ex == null || ex == 0) {
                jdbc.update("INSERT INTO ats_interview_process_template_share_to (interviewprocesstemplate_id, user_id) VALUES (?,?)",
                        iptId, u);
            }
        }
        return ApiResult.success(true);
    }

    private long insert(String sql, Object... args) {
        GeneratedKeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            return ps;
        }, kh);
        return Objects.requireNonNull(kh.getKey()).longValue();
    }

    private static Timestamp parseTs(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.replace('T', ' ');
        if (t.endsWith("Z")) t = t.substring(0, t.length() - 1);
        if (t.length() > 19) t = t.substring(0, 19);
        return Timestamp.valueOf(t);
    }

    private static LocalDateTime toLdt(Object o) {
        if (o instanceof Timestamp t) return t.toLocalDateTime();
        if (o instanceof LocalDateTime l) return l;
        return null;
    }

    private static boolean toBool(Object o) {
        if (o instanceof Boolean b) return b;
        return o instanceof Number n && n.intValue() != 0;
    }

    private Object parseJson(Object o) {
        if (o == null) return null;
        try {
            return mapper.readValue(o.toString(), Object.class);
        } catch (Exception e) {
            return null;
        }
    }
}
