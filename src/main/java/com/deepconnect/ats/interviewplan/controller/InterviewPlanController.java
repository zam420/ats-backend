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
import com.deepconnect.ats.interviewplan.model.InterviewPlanRoundType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import org.springframework.http.HttpStatus;

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
        return ApiResult.success(Collections.singletonMap("id", interviewPlanService.create(req)));
    }

    @PreAuthorize("hasAuthority('iptc')")
    @PostMapping("/saveas")
    public ApiResult<Map<String, String>> saveAs(@RequestBody InterviewPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", interviewPlanService.create(req)));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody InterviewPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", interviewPlanService.update(req)));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/deactivate/{id}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        return ApiResult.success(interviewPlanService.deactivate(id));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/{id}")
    public ApiResult<InterviewPlanDetailsRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(interviewPlanService.findByPublicIdForTenant(id));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/roundtypes")
    public ApiResult<List<InterviewPlanRoundType>> getAllRoundTypes() throws Exception {
        return ApiResult.success(interviewPlanService.getAllRoundTypes());
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<InterviewPlanRes>> getAll(AtsPageQuery query) {
        return ApiResult.success(interviewPlanService.getAll(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/owned")
    public ApiResult<AtsPageRsp<InterviewPlanRes>> getAllForPlanOwner(AtsPageQuery query) {
        return ApiResult.success(interviewPlanService.getAllForPlanOwner(query));
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

    @PreAuthorize("hasAuthority('iptsa')")
    @PostMapping("/templates/create")
    public ApiResult<Map<String, String>> createTemplate(@RequestBody InterviewPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", interviewPlanService.createTemplate(req)));
    }

    @PreAuthorize("hasAuthority('iptsa')")
    @PostMapping("/templates/update")
    public ApiResult<Map<String, String>> updateTemplate(@RequestBody InterviewPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", interviewPlanService.updateTemplate(req)));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/listByJob")
    public ApiResult<List<InterviewPlanRes>> listProcessTemplateByJob(@RequestParam(required = false) Long jobId) {
        throw new UnsupportedOperationException("Listing process templates by job is not implemented yet.");
        // return ApiResult.success(interviewPlanService.listProcessTemplates()); //TODO: bug in original code, not returning results by jobId param
    }

}
