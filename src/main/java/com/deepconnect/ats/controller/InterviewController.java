package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.*;
import com.deepconnect.ats.model.InterviewFeedback;
import com.deepconnect.ats.model.InterviewProcessTemplate;
import com.deepconnect.ats.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    // Process Template Endpoints
    @PreAuthorize("hasAnyRole('sa','proa')")
    @PostMapping("/processTemp/save")
    public ApiResult<Map<String, Long>> saveProcessTemplate(@RequestBody ProcessTemplateReq req) {
        Long id = interviewService.saveProcessTemplate(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','proa')")
    @PostMapping("/processTemp/toggleStatus")
    public ApiResult<Boolean> toggleProcessTemplateStatus(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("ipt_id").toString());
        Integer status = Integer.valueOf(body.get("status").toString());
        interviewService.toggleProcessTemplateStatus(id, status);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/processTemp/page")
    public ApiResult<AtsPageRsp<InterviewProcessTemplate>> pageProcessTemplate(AtsPageQuery query) {
        return ApiResult.success(interviewService.pageProcessTemplates(query));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/processTemp/detail")
    public ApiResult<InterviewProcessTemplate> processTemplateDetail(@RequestParam Long id) {
        return ApiResult.success(interviewService.getProcessTemplateById(id).orElse(null));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/processTemp/listBy")
    public ApiResult<List<InterviewProcessTemplate>> listProcessTemplateBy() {
        return ApiResult.success(interviewService.listProcessTemplates());
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/processTemp/listByJob")
    public ApiResult<List<InterviewProcessTemplate>> listProcessTemplateByJob(@RequestParam(required = false) Long jobId) {
        return ApiResult.success(interviewService.listProcessTemplates());
    }

    // Interview Execution Endpoints
    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/setup")
    public ApiResult<Map<String, Long>> setup(@RequestBody InterviewSetupReq req) {
        Long id = interviewService.setupInterview(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/submit")
    public ApiResult<Boolean> submit(@RequestBody InterviewSubmitReq req) {
        interviewService.submitInterview(req);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/cancel")
    public ApiResult<Boolean> cancel(@RequestBody Map<String, Long> body) {
        Long id = body.get("interviewId");
        if (id != null) {
            interviewService.cancelInterview(id);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/detail")
    public ApiResult<InterviewDetailRsp> detail(@RequestParam Long id) {
        return ApiResult.success(interviewService.getInterviewDetail(id));
    }

    // Feedback Endpoints
    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @PostMapping("/feedback/submit")
    public ApiResult<Map<String, Long>> submitFeedback(@RequestBody InterviewFeedbackReq req) {
        Long id = interviewService.submitFeedback(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/feedback/detail")
    public ApiResult<InterviewFeedback> feedbackDetail(@RequestParam Long id) {
        return ApiResult.success(interviewService.getFeedbackDetail(id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/feedback/listByResume")
    public ApiResult<List<InterviewFeedback>> feedbackListByResume(@RequestParam Long candidateResumeId) {
        return ApiResult.success(interviewService.listFeedbackByResume(candidateResumeId));
    }
}
