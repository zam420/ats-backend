package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.service.UserService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.DuplicationCheckBody;
import com.deepconnect.ats.dto.auth.LoggedInUserDetails;
import com.deepconnect.ats.model.AtsEmailRecord;
import com.deepconnect.ats.model.CandidateResumeDuplication;
import com.deepconnect.ats.model.User;
import com.deepconnect.ats.service.CandidateDuplicationService;
import com.deepconnect.ats.service.FileStorageService;
import com.deepconnect.ats.service.JobPositionImportService;
import com.deepconnect.ats.service.auth.AuthService;
import com.deepconnect.ats.service.mail.MailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class RecruitmentExtController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] RESUME_STATUS = {"TALENT_POOL", "NEW", "SCREENING", "EXAM", "INTERVIEW", "OFFER",
            "ONBOARDING", "HIRED", "REJECTED"};

    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final CandidateDuplicationService duplicationService;
    private final UserService userService;
    private final MailService mailService;
    private final FileStorageService fileStorageService;
    private final JobPositionImportService jobImportService;

    private static Map<String, Object> vn(Object v, String n) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("value", v);
        m.put("name", n);
        return m;
    }

    private static String fmt(Object o) {
        if (o == null) return null;
        if (o instanceof java.sql.Timestamp t) return t.toLocalDateTime().format(FMT);
        if (o instanceof LocalDateTime l) return l.format(FMT);
        return o.toString();
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/listInterviewers")
    public ApiResult<Object> listInterviewers() {
        return ApiResult.success(jdbc.queryForList(
                "SELECT DISTINCT u.id, u.name, u.email FROM ats_user u JOIN ats_user_role ur ON ur.user_id = u.id "
                        + "JOIN ats_role r ON r.id = ur.role_id WHERE u.status = 0 AND r.code = 'itv'"));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/listForInterview")
    public ApiResult<Object> listForInterview(@RequestParam(value = "job_position_id", required = false) Long jobId) {
        if (jobId == null) throw new InternalServerException("job_position_id is required");
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM ats_job_position WHERE id = ?", Integer.class, jobId);
        if (cnt == null || cnt == 0) throw new InternalServerException("Job position not found");
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT cr.id, cr.email, cr.name, cr.phone_number, cr.applied_at FROM ats_candidate_resume cr "
                        + "LEFT JOIN ats_interview_candidate ic ON cr.id = ic.candidate_resume_id "
                        + "WHERE cr.applying_position_id = ? AND cr.status <> -1 AND (ic.status IS NULL OR ic.status IN (-1,4,3)) "
                        + "GROUP BY cr.id, cr.email, cr.name, cr.phone_number, cr.applied_at", jobId);
        rows.forEach(r -> r.put("applied_at", fmt(r.get("applied_at"))));
        return ApiResult.success(rows);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/listScreenFeedback")
    public ApiResult<Object> listScreenFeedback(@RequestParam(value = "candidate_resume_id", required = false) Long id) {
        if (id == null) throw new InternalServerException("candidate_resume_id is required");
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM ats_candidate_resume WHERE id = ?", Integer.class, id);
        if (cnt == null || cnt == 0) throw new InternalServerException("Resume not found");
        String[] names = {"PENDING", "SUITABLE", "UNSUITABLE", "KEEP_IN_VIEW"};
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> r : jdbc.queryForList(
                "SELECT f.feedback_notes, f.status, f.updated_at, f.to_user_id, u.name user_name, f.resume_position_id, jp.title "
                        + "FROM ats_candidate_resume_forward f LEFT JOIN ats_user u ON u.id = f.to_user_id "
                        + "LEFT JOIN ats_job_position jp ON jp.id = f.resume_position_id "
                        + "WHERE f.candidate_resume_id = ? AND f.status != 0 ORDER BY f.updated_at DESC", id)) {
            int s = ((Number) r.get("status")).intValue();
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("notes", r.get("feedback_notes"));
            m.put("status", s);
            m.put("status_name", s >= 0 && s < names.length ? names[s] : String.valueOf(s));
            m.put("feedback_at", fmt(r.get("updated_at")));
            m.put("feedback_user_id", r.get("to_user_id"));
            m.put("feedback_user_name", r.get("user_name"));
            m.put("job_id", r.get("resume_position_id"));
            m.put("job_title", r.get("title"));
            out.add(m);
        }
        return ApiResult.success(out);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/listApplicationByCandidate")
    public ApiResult<Object> listApplicationByCandidate(@RequestParam("candidate_id") Long candidateId) {
        Long uid = AuthService.getLoggedInUserId();
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> r : jdbc.queryForList(
                "SELECT cr.id, cr.applied_at, cr.applying_position_id, jp.title, cr.recruitment_source, cr.status, "
                        + "cr.create_by_id, u.name creator_name FROM ats_candidate_resume cr "
                        + "LEFT JOIN ats_job_position jp ON jp.id = cr.applying_position_id "
                        + "LEFT JOIN ats_user u ON u.id = cr.create_by_id "
                        + "WHERE cr.candidate_id = ? AND (cr.data_status = 1 OR (cr.data_status = 0 AND cr.create_by_id = ?)) "
                        + "ORDER BY cr.applied_at DESC", candidateId, uid)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("application_progress", new ArrayList<>());
            m.put("applied_at", fmt(r.get("applied_at")));
            m.put("applying_position", r.get("applying_position_id"));
            m.put("applying_position_title", r.get("title"));
            m.put("candidate_resume_id", r.get("id"));
            m.put("hr_recruiter_id", r.get("create_by_id"));
            m.put("hr_recruiter_name", r.get("creator_name"));
            m.put("pooled_reason", null);
            m.put("project_id", null);
            m.put("project_name", null);
            Object src = r.get("recruitment_source");
            m.put("recruitment_source", src);
            m.put("recruitment_source_name", src == null ? null : "SOURCE_" + src);
            m.put("recruitment_type", null);
            m.put("recruitment_type_name", null);
            int s = r.get("status") == null ? 0 : ((Number) r.get("status")).intValue();
            m.put("status", s);
            m.put("status_name", s + 1 >= 0 && s + 1 < RESUME_STATUS.length ? RESUME_STATUS[s + 1] : String.valueOf(s));
            out.add(m);
        }
        return ApiResult.success(out);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/listEmailRecords")
    public ApiResult<Object> listEmailRecords(@RequestParam("resume_id") Long resumeId) {
        String[] statusNames = {"PENDING", "SENT", "FAILED"};
        List<Map<String, Object>> toCandidate = new ArrayList<>();
        List<Map<String, Object>> toEmployee = new ArrayList<>();
        String like1 = "%\"candidate_resume_id\": " + resumeId + "%";
        String like2 = "%\"candidate_resume_id\":" + resumeId + "%";
        for (Map<String, Object> r : jdbc.queryForList(
                "SELECT e.*, u.name creator_name, u.email creator_email FROM ats_email_records e "
                        + "LEFT JOIN ats_user u ON u.id = e.from_user_id "
                        + "WHERE CAST(e.trigger_models AS CHAR) LIKE ? OR CAST(e.trigger_models AS CHAR) LIKE ?",
                like1, like2)) {
            Map<String, Object> m = new LinkedHashMap<>();
            Object sched = r.get("scheduled_time") != null ? r.get("scheduled_time") : r.get("created_at");
            m.put("send_time", fmt(sched));
            int s = r.get("status") == null ? 0 : ((Number) r.get("status")).intValue();
            m.put("status", s);
            m.put("status_name", s >= 0 && s < statusNames.length ? statusNames[s] : String.valueOf(s));
            m.put("creator_id", r.get("from_user_id"));
            m.put("creator_email", r.get("creator_email"));
            m.put("creator_name", r.get("creator_name"));
            Object to = parse(r.get("to"));
            m.put("to", to);
            m.put("cc", parse(r.get("cc")));
            m.put("bcc", parse(r.get("bcc")));
            m.put("subject", r.get("subject"));
            m.put("content", HtmlUtils.htmlEscape(String.valueOf(r.get("body"))));
            Object h = r.get("is_html_mail");
            m.put("is_html_mail", h instanceof Boolean b ? b : h instanceof Number n && n.intValue() != 0);
            boolean external = false;
            if (to instanceof List<?> l) {
                for (Object a : l) if (!String.valueOf(a).toLowerCase().endsWith("@espressif.com")) external = true;
            }
            (external ? toCandidate : toEmployee).add(m);
        }
        Comparator<Map<String, Object>> c = (a, b) -> String.valueOf(b.get("send_time")).compareTo(String.valueOf(a.get("send_time")));
        toCandidate.sort(c);
        toEmployee.sort(c);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("to_candidate", toCandidate);
        res.put("to_employee", toEmployee);
        return ApiResult.success(res);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/sendMailToCantidate")
    public ApiResult<Object> sendMailToCandidate(@RequestBody Map<String, Object> body) {
        Long uid = AuthService.getLoggedInUserId();
        JsonNode n = mapper.valueToTree(body);
        List<String> bcc = new ArrayList<>();
        for (JsonNode u : n.path("bcc_users")) {
            List<String> e = jdbc.queryForList("SELECT email FROM ats_user WHERE id = ?", String.class, u.asLong());
            if (e.isEmpty()) throw new InternalServerException("User not found: " + u.asLong());
            bcc.add(e.get(0));
        }
        List<AtsEmailRecord> records = new ArrayList<>();
        int i = 0;
        LocalDateTime base = LocalDateTime.now();
        for (JsonNode c : n.path("candidate_mails")) {
            long rid = c.path("candidate_resume_id").asLong();
            List<String> e = jdbc.queryForList("SELECT email FROM ats_candidate_resume WHERE id = ?", String.class, rid);
            if (e.isEmpty()) throw new InternalServerException("Resume not found: " + rid);
            Map<String, Object> trig = new LinkedHashMap<>();
            trig.put("request_id", n.path("request_id").asText(null));
            trig.put("candidate_resume_id", rid);
            AtsEmailRecord rec = AtsEmailRecord.builder()
                    .to(List.of(e.get(0)))
                    .bcc(bcc)
                    .subject(c.path("email_subject").asText(""))
                    .body(c.path("email_body").asText(""))
                    .triggerModels(trig)
                    .mailType(4)
                    .fromUserId(uid)
                    .scheduledTime(base.plusMinutes(5L * (i / 100)))
                    .build();
            records.add(rec);
            i++;
        }
        mailService.sendInBulk(records, false);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @PostMapping("/checkResumeDuplication")
    public ApiResult<Object> checkResumeDuplication(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(String.valueOf(body.get("id")));
        LoggedInUserDetails userDetails = AuthService.getLoggedInUser();
        if (userDetails == null) {
            return ApiResult.fail(ApiResult.UNAUTHORIZED_CODE, "Unauthorized");
        }
        User user = userService.getById(userDetails.getUserId());
        return ApiResult.success(duplicationService.checkResumeDuplication(
                new DuplicationCheckBody(user, id)));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/getResumeDuplication")
    public ApiResult<Object> getResumeDuplication(@RequestParam("id") Long id) {
        CandidateResumeDuplication d = duplicationService.getDuplicationByResumeId(id);
        return ApiResult.success(d == null || d.getDuplicateResumes() == null ? new ArrayList<>() : d.getDuplicateResumes());
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/existResumes")
    public ApiResult<Object> existResumes(@RequestParam(value = "phone_number", required = false) String phone,
                                          @RequestParam(value = "email", required = false) String email,
                                          @RequestParam(value = "exclude_id", defaultValue = "0") Long excludeId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("phone_number", exists("phone_number", phone, excludeId));
        m.put("email", exists("email", email, excludeId));
        return ApiResult.success(m);
    }

    private boolean exists(String col, String val, Long excludeId) {
        if (val == null || val.isBlank()) return false;
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM ats_candidate_resume WHERE " + col
                + " = ? AND data_status != 2 AND id != ?", Integer.class, val, excludeId);
        return c != null && c > 0;
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/coverDuplicateResumes")
    @Transactional
    public ApiResult<Object> coverDuplicateResumes(@RequestBody Map<String, Object> body) {
        JsonNode n = mapper.valueToTree(body);
        long src = n.path("src_resume_id").asLong();
        List<Long> dstResumes = new ArrayList<>();
        n.path("dst_resume_ids").forEach(x -> dstResumes.add(x.asLong()));
        boolean hasCand = n.hasNonNull("dst_candidate_id");
        if (dstResumes.contains(src)) throw new InternalServerException("Source resume can not be a destination");
        if (hasCand == !dstResumes.isEmpty()) {
            throw new InternalServerException("Exactly one of dst_candidate_id and dst_resume_ids is required");
        }
        List<Long> srcCand = jdbc.queryForList("SELECT candidate_id FROM ats_candidate_resume WHERE id = ?", Long.class, src);
        if (srcCand.isEmpty()) throw new InternalServerException("Source resume not found");
        if (hasCand) {
            long dst = n.get("dst_candidate_id").asLong();
            if (srcCand.get(0) == dst) throw new InternalServerException("Source and destination candidate are the same");
            jdbc.update("UPDATE ats_candidate_resume SET candidate_id = ? WHERE id = ?", dst, src);
        } else {
            jdbc.update("UPDATE ats_candidate_resume SET data_status = 2 WHERE id = ?", src);
            for (String t : new String[]{"ats_candidate_education_history", "ats_candidate_work_experience",
                    "ats_candidate_project_experience"}) {
                try {
                    jdbc.update("UPDATE " + t + " SET data_status = 2 WHERE candidate_resume_id = ?", src);
                } catch (Exception ignored) {
                    // table may not have these columns
                }
            }
        }
        Integer left = jdbc.queryForObject(
                "SELECT COUNT(*) FROM ats_candidate_resume WHERE candidate_id = ? AND data_status != 2", Integer.class, srcCand.get(0));
        if (left != null && left == 0) {
            try {
                jdbc.update("UPDATE ats_candidate SET data_status = 2 WHERE id = ?", srcCand.get(0));
            } catch (Exception ignored) {
                // ignore
            }
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/uploadCandidateResumeFile")
    public ApiResult<Object> uploadCandidateResumeFile(
            @RequestParam(value = "original_resumes", required = false) List<MultipartFile> originalResumes,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments) {
        Long userId = AuthService.getLoggedInUserId();
        List<Map<String, Object>> originals = new ArrayList<>();
        if (originalResumes != null) {
            for (MultipartFile f : originalResumes) {
                originals.add(fileStorageService.upload(userId, f, FileStorageService.RESUME_ROOT_PATH));
            }
        }
        List<Map<String, Object>> atts = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile f : attachments) {
                atts.add(fileStorageService.upload(userId, f, FileStorageService.RESUME_ATTACHMENT_PATH));
            }
        }
        Map<String, Object> rsp = new LinkedHashMap<>();
        rsp.put("original_resume", originals);
        rsp.put("attachments", atts);
        rsp.put("pdf_details", null);
        return ApiResult.success(rsp);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa')")
    @PostMapping("/jobUpload")
    public ApiResult<Object> jobUpload(@RequestParam("file") MultipartFile file) {
        jobImportService.importJobs(file);
        return ApiResult.success(null);
    }

    private Object parse(Object o) {
        if (o == null) return null;
        try {
            return mapper.readValue(o.toString(), Object.class);
        } catch (Exception e) {
            return o.toString();
        }
    }
}
