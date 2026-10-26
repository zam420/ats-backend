package com.deepconnect.ats.interviewplan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

import com.deepconnect.ats.interviewplan.dto.InterviewPlanReq;
import com.deepconnect.ats.interviewplan.service.InterviewPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.interviewplan.dto.InterviewPlanRes;
import com.deepconnect.ats.interviewplan.dto.InterviewPlanDetailsRes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviewplan")
@RequiredArgsConstructor
@Validated // 1. Required at the class level for path variable/request param validation
public class InterviewPlanController {

    private final InterviewPlanService interviewPlanService;

    @PreAuthorize("hasAuthority('iptc')")
    @PostMapping("/create")
    public ApiResult<Map<String, String>> create(@RequestBody InterviewPlanReq req) {
        String id = interviewPlanService.create(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody InterviewPlanReq req) {
        String id = interviewPlanService.update(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/deactivate/{id}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        interviewPlanService.deactivate(id);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/{id}")
    public ApiResult<InterviewPlanDetailsRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(interviewPlanService.findByPublicIdForTenant(id));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<InterviewPlanRes>> getAll(AtsPageQuery query) {
        return ApiResult.success(interviewPlanService.getAll(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/shared")
    public ApiResult<AtsPageRsp<InterviewPlanRes>> getAllSharedWithUser(AtsPageQuery query) {
        return ApiResult.success(interviewPlanService.getAllSharedWithUser(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/templates")
    public ApiResult<AtsPageRsp<InterviewPlanRes>> getAllSystemTemplates(AtsPageQuery query) {
        return ApiResult.success(interviewPlanService.getAllSystemTemplates(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/listByJob")
    public ApiResult<List<InterviewPlanRes>> listProcessTemplateByJob(@RequestParam(required = false) Long jobId) {
        throw new UnsupportedOperationException("Listing process templates by job is not implemented yet.");
        // return ApiResult.success(interviewPlanService.listProcessTemplates()); //TODO: bug in original code, not returning results by jobId param
    }

}
