package com.deepconnect.ats.service;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

/**
 * Imports job positions from an Excel sheet. Column order (header row first):
 * title, project, recruitment_type, employment_type, job_type, work_location, job_desc,
 * job_req, hr_recruiter_email, hr_coordinators, resume_inbox, publish_channel.
 */
@Service
@RequiredArgsConstructor
public class JobPositionImportService {

    private static final Map<String, Integer> RECRUITMENT_TYPE = Map.of("CAMPUS", 0, "CORPORATE", 1, "INTERN", 2);
    private static final Map<String, Integer> EMPLOYMENT_TYPE =
            Map.of("FULL_TIME", 0, "PART_TIME", 1, "CONTRACT", 2, "INTERNSHIP", 3);
    private static final Map<String, Integer> JOB_TYPE = Map.of("SOC", 0, "AI", 1, "HARDWARE", 2,
            "SOFTWARE", 3, "GENERAL", 4, "BUSINESS", 5, "PRODUCTION", 6);
    private static final Map<String, Integer> WORK_LOCATION = new LinkedHashMap<>();
    private static final Set<String> RESUME_INBOX = Set.of("campus@espressif.com", "recruit@espressif.com",
            "campus.in@espressif.com", "recruit.in@espressif.com", "recruit.sgpr@espressif.com",
            "recruit.cz@espressif.com", "recruit.br@espressif.com", "recruit.jp@espressif.com");
    private static final Map<String, String> CHANNEL = Map.of("LiePin", "LIE_PIN",
            "Official Website EN", "OFFICIAL_WEBSITE_EN", "Official Website CN", "OFFICIAL_WEBSITE_CN",
            "LinkedIn", "LINKED_IN");
    private static final int COLUMNS = 12;

    static {
        String[] names = {"REMOTE", "CHINA", "SHANGHAI", "WUXI", "HEFEI", "SUZHOU", "SHENZHEN", "CZECH", "BRNO",
                "INDIA", "PUNE", "SINGAPORE", "BRAZIL", "CAMPINAS", "JAPAN", "TOKYO", "OSAKA", "YOKOHAMA"};
        int[] values = {0, 1, 2, 3, 4, 5, 6, 500, 501, 600, 601, 700, 800, 801, 900, 901, 902, 903};
        for (int i = 0; i < names.length; i++) WORK_LOCATION.put(names[i], values[i]);
    }

    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    private record Row(String title, long projectId, int recruitmentType, int employmentType, int jobType,
                       String workLocation, String jobDesc, String jobReq, long hrRecruiterId, String resumeInbox,
                       String publishChannel, List<Long> coordinators) {
    }

    @Transactional
    public int importJobs(MultipartFile file) {
        Long userId = AuthService.getLoggedInUserId();
        if (userId == null) userId = 1L;
        List<List<String>> rows = readRows(file);

        Map<Integer, String> errors = new LinkedHashMap<>();
        List<Row> valid = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < rows.size(); i++) {
            int rowNo = i + 2; // Excel row number (header is row 1)
            try {
                Row r = parse(rows.get(i), userId);
                if (!seen.add(r.title + "|" + r.projectId)) {
                    throw new IllegalArgumentException("Duplicated job title found in the project");
                }
                valid.add(r);
            } catch (IllegalArgumentException e) {
                errors.put(rowNo, e.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            try {
                throw new InternalServerException(mapper.writeValueAsString(errors));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new InternalServerException(errors.toString());
            }
        }

        for (Row r : valid) {
            KeyHolder kh = new GeneratedKeyHolder();
            final long uid = userId;
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO ats_job_position (title, project_id, recruitment_type, employment_type, job_type, "
                                + "work_location, job_desc, job_req, hr_recruiter_id, resume_inbox, publish_channel, "
                                + "created_by_id, created_at, updated_at, status, public_interview_feedback) "
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,NOW(6),NOW(6),0,0)",
                        Statement.RETURN_GENERATED_KEYS);
                int k = 1;
                ps.setString(k++, r.title);
                ps.setLong(k++, r.projectId);
                ps.setInt(k++, r.recruitmentType);
                ps.setInt(k++, r.employmentType);
                ps.setInt(k++, r.jobType);
                ps.setString(k++, r.workLocation);
                ps.setString(k++, r.jobDesc);
                ps.setString(k++, r.jobReq);
                ps.setLong(k++, r.hrRecruiterId);
                ps.setString(k++, r.resumeInbox);
                ps.setString(k++, r.publishChannel);
                ps.setLong(k, uid);
                return ps;
            }, kh);
            long jobId = Objects.requireNonNull(kh.getKey()).longValue();
            for (Long c : r.coordinators) {
                jdbc.update("INSERT INTO ats_job_position_hr_coordinators (jobposition_id, user_id) VALUES (?,?)",
                        jobId, c);
            }
        }
        return valid.size();
    }

    private Row parse(List<String> c, long userId) {
        String title = c.get(0);
        if (title.isEmpty()) throw new IllegalArgumentException("title can not be empty");
        if (title.length() > 50) throw new IllegalArgumentException("Title must not exceed 50 characters in length.");

        long projectId = resolveProject(c.get(1), userId);
        Integer recruitment = RECRUITMENT_TYPE.get(c.get(2).toUpperCase());
        if (recruitment == null) throw new IllegalArgumentException("Illegal recruitment_type");
        Integer employment = EMPLOYMENT_TYPE.get(c.get(3).toUpperCase());
        if (employment == null) throw new IllegalArgumentException("Illegal employment_type");
        Integer jobType = JOB_TYPE.get(c.get(4).toUpperCase());
        if (jobType == null) throw new IllegalArgumentException("Illegal job_type");

        List<Integer> locations = new ArrayList<>();
        for (String s : split(c.get(5))) {
            Integer v = WORK_LOCATION.get(s.toUpperCase());
            if (v == null) throw new IllegalArgumentException("Illegal work_location: " + s);
            locations.add(v);
        }
        if (locations.isEmpty()) throw new IllegalArgumentException("work_location can not be empty");

        if (c.get(6).isEmpty()) throw new IllegalArgumentException("job_desc can not be empty");
        if (c.get(7).isEmpty()) throw new IllegalArgumentException("job_req can not be empty");
        if (c.get(8).isEmpty()) throw new IllegalArgumentException("hr_recruiter_email can not be empty");

        Long recruiterId = findUserWithRole(c.get(8), "hrr");
        if (recruiterId == null) throw new IllegalArgumentException("HR Recruiter not found: " + c.get(8));

        List<Long> coordinators = new ArrayList<>();
        List<String> coordEmails = split(c.get(9));
        if (coordEmails.size() > 10) {
            throw new IllegalArgumentException("Too many HR Coordinator, a single job can set up to 10 HR Coordinator");
        }
        for (String email : coordEmails) {
            Long id = findUserWithRole(email, "hrrc");
            if (id == null) throw new IllegalArgumentException("HR Recruiter Coordinator not found: " + email);
            if (!coordinators.contains(id)) coordinators.add(id);
        }

        if (!RESUME_INBOX.contains(c.get(10))) throw new IllegalArgumentException("Illegal resume_inbox");

        List<String> channels = new ArrayList<>();
        for (String s : split(c.get(11))) {
            String ch = CHANNEL.get(s);
            if (ch == null) throw new IllegalArgumentException("Illegal publish_channel: " + s);
            channels.add(ch);
        }

        Integer dup = jdbc.queryForObject(
                "SELECT COUNT(*) FROM ats_job_position WHERE project_id = ? AND title = ?", Integer.class,
                projectId, title);
        if (dup != null && dup > 0) throw new IllegalArgumentException("Duplicated job title found in the project");

        try {
            return new Row(title, projectId, recruitment, employment, jobType, mapper.writeValueAsString(locations),
                    c.get(6), c.get(7), recruiterId, c.get(10),
                    channels.isEmpty() ? null : mapper.writeValueAsString(channels), coordinators);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private long resolveProject(String name, long userId) {
        if (name.isEmpty()) throw new IllegalArgumentException("project can not be empty");
        List<Map<String, Object>> p = jdbc.queryForList(
                "SELECT id, status, created_by_id FROM ats_recruitment_project WHERE name = ?", name);
        if (p.isEmpty()) throw new IllegalArgumentException("project not found: " + name);
        Map<String, Object> proj = p.get(0);
        if (((Number) proj.get("status")).intValue() != 0) {
            throw new IllegalArgumentException("project is not active: " + name);
        }
        long id = ((Number) proj.get("id")).longValue();
        boolean sa = hasRole(userId, "sa");
        boolean hrd = hasRole(userId, "hrd");
        boolean proa = hasRole(userId, "proa");
        boolean owner = ((Number) proj.get("created_by_id")).longValue() == userId;
        Integer admin = jdbc.queryForObject(
                "SELECT COUNT(*) FROM ats_recruitment_project_admins WHERE recruitmentproject_id = ? AND user_id = ?",
                Integer.class, id, userId);
        boolean isAdmin = admin != null && admin > 0;
        boolean ok = sa || (hrd && owner) || (proa && isAdmin);
        if (!ok) throw new IllegalArgumentException("Have no permission to operate this recruitment project");
        return id;
    }

    private boolean hasRole(long userId, String code) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM ats_user_role ur JOIN ats_role r ON r.id = ur.role_id "
                + "WHERE ur.user_id = ? AND r.code IN (?, 'sa')", Integer.class, userId, code);
        return n != null && n > 0;
    }

    private Long findUserWithRole(String email, String roleCode) {
        List<Long> ids = jdbc.queryForList("SELECT u.id FROM ats_user u JOIN ats_user_role ur ON ur.user_id = u.id "
                + "JOIN ats_role r ON r.id = ur.role_id WHERE u.email = ? AND r.code = ?", Long.class, email, roleCode);
        return ids.isEmpty() ? null : ids.get(0);
    }

    private static List<String> split(String s) {
        List<String> out = new ArrayList<>();
        if (s == null) return out;
        for (String p : s.split("[,，]")) {
            if (!p.trim().isEmpty()) out.add(p.trim());
        }
        return out;
    }

    private List<List<String>> readRows(MultipartFile file) {
        List<List<String>> rows = new ArrayList<>();
        try (InputStream in = file.getInputStream(); Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                if (row == null) continue;
                List<String> cells = new ArrayList<>();
                boolean empty = true;
                for (int j = 0; j < COLUMNS; j++) {
                    Cell cell = row.getCell(j);
                    String v = cell == null ? "" : fmt.formatCellValue(cell).trim();
                    if (!v.isEmpty()) empty = false;
                    cells.add(v);
                }
                if (!empty) rows.add(cells);
            }
        } catch (Exception e) {
            throw new InternalServerException("Failed to read Excel file: " + e.getMessage());
        }
        if (rows.isEmpty()) throw new InternalServerException("The Excel file contains no data rows");
        return rows;
    }
}
