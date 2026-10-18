package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.model.Exam;
import com.deepconnect.ats.model.ExamForward;
import com.deepconnect.ats.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/nowcoder/getPapers")
    public ApiResult<List<Map<String, Object>>> getPapers() {
        return ApiResult.success(Collections.emptyList());
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/nowcoder/checkBalance")
    public ApiResult<Map<String, Object>> checkBalance() {
        return ApiResult.success(Collections.singletonMap("balance", 1000));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/nowcoder/createTest")
    public ApiResult<Map<String, Long>> createTest(@RequestBody Exam exam) {
        Long id = examService.createExam(exam);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/nowcoder/deleteTest")
    public ApiResult<Boolean> deleteTest(@RequestBody Map<String, Long> body) {
        Long id = body.get("examId");
        if (id != null) {
            examService.cancelExam(id);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/nowcoder/listTest")
    public ApiResult<AtsPageRsp<Exam>> listTest(AtsPageQuery query) {
        return ApiResult.success(examService.listExams(query));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/cancelExam")
    public ApiResult<Boolean> cancelExam(@RequestBody Map<String, Long> body) {
        Long id = body.get("examId");
        if (id != null) {
            examService.cancelExam(id);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/createForward")
    public ApiResult<Map<String, Long>> createForward(@RequestBody Map<String, Object> body) {
        Long examId = Long.valueOf(body.get("examId").toString());
        Long candidateResumeId = Long.valueOf(body.get("candidateResumeId").toString());
        @SuppressWarnings("unchecked")
        List<Long> toUserIds = (List<Long>) body.get("toUserIds");
        Long id = examService.createForward(examId, candidateResumeId, toUserIds);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/listForward")
    public ApiResult<List<ExamForward>> listForward(@RequestParam Long candidateResumeId) {
        return ApiResult.success(examService.listForward(candidateResumeId));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','itv')")
    @PostMapping("/submitForwordFeedback")
    public ApiResult<Boolean> submitForwardFeedback(@RequestBody Map<String, Object> body) {
        Long forwardId = Long.valueOf(body.get("forwardId").toString());
        String feedbackNotes = body.get("feedbackNotes") != null ? body.get("feedbackNotes").toString() : "";
        examService.submitForwardFeedback(forwardId, feedbackNotes);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/submitHRFeedback")
    public ApiResult<Boolean> submitHRFeedback(@RequestBody Map<String, Object> body) {
        Long examId = Long.valueOf(body.get("examId").toString());
        Integer hrFeedback = Integer.valueOf(body.get("hrFeedback").toString());
        examService.submitHRFeedback(examId, hrFeedback);
        return ApiResult.success(true);
    }
}
