package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deepconnect.ats.dto.ApiResult;

import java.util.*;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamExtController {

    private final JdbcTemplate jdbc;

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/listForwardFeedback")
    public ApiResult<Object> listForwardFeedback(@RequestParam("resume_id") Long resumeId) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT ef.to_user_id, u.name user_name, ef.feedback_notes, ef.status, ef.created_at, "
                        + "e.job_position_id job_id, jp.title job_title "
                        + "FROM ats_exam_forward ef LEFT JOIN ats_user u ON u.id = ef.to_user_id "
                        + "LEFT JOIN ats_exam e ON e.id = ef.exam_id "
                        + "LEFT JOIN ats_job_position jp ON jp.id = e.job_position_id "
                        + "WHERE ef.candidate_resume_id = ? AND ef.data_status = 1 AND ef.status != 0 "
                        + "ORDER BY ef.created_at DESC", resumeId);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("feedback_user_id", r.get("to_user_id"));
            m.put("feedback_user_name", r.get("user_name"));
            m.put("feedback_notes", r.get("feedback_notes"));
            int s = ((Number) r.get("status")).intValue();
            Map<String, Object> st = new LinkedHashMap<>();
            st.put("value", s);
            st.put("name", s == 1 ? "SUITABLE" : "UNSUITABLE");
            m.put("feedback_status", st);
            m.put("feedback_time", r.get("created_at") == null ? null : r.get("created_at").toString().replace('T', ' '));
            m.put("job_id", r.get("job_id"));
            m.put("job_title", r.get("job_title"));
            out.add(m);
        }
        return ApiResult.success(out);
    }
}
