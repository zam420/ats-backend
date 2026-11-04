package com.deepconnect.ats.recruitmentpipeline.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;

import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineReq;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRes;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineDetailsRes;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitmentpipeline")
@RequiredArgsConstructor
@Validated // 1. Required at the class level for path variable/request param validation
public class RecruitmentPipelineController {

    private final RecruitmentPipelineService recruitmentPipelineService;

    @PreAuthorize("hasAuthority('rptc')")
    @PostMapping("/create")
    public ApiResult<Map<String, String>> create(@RequestBody @Valid RecruitmentPipelineReq req) {
        return ApiResult.success(Collections.singletonMap("id", recruitmentPipelineService.create(req)));
    }

    @PreAuthorize("hasAuthority('rptc')")
    @PostMapping("/saveas")
    public ApiResult<Map<String, String>> saveAs(@RequestBody @Valid RecruitmentPipelineReq req) {
        return ApiResult.success(Collections.singletonMap("id", recruitmentPipelineService.create(req)));
    }

    @PreAuthorize("hasAuthority('rpte')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody @Valid RecruitmentPipelineReq req) {
        return ApiResult.success(Collections.singletonMap("id", recruitmentPipelineService.update(req)));
    }

    @PreAuthorize("hasAuthority('rpte')")
    @PostMapping("/deactivate/{id}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        return ApiResult.success(recruitmentPipelineService.deactivate(id));
    }

    @PreAuthorize("hasAuthority('rpts')")
    @GetMapping("/{id}")
    public ApiResult<RecruitmentPipelineDetailsRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(recruitmentPipelineService.findByPublicIdForTenant(id));
    }

    @PreAuthorize("hasAuthority('rpts')")
    @GetMapping("/roundtypes")
    public ApiResult<List<RecruitmentPipelineRoundType>> getAllRoundTypes() throws Exception {
        return ApiResult.success(recruitmentPipelineService.getAllRoundTypes());
    }

    @PreAuthorize("hasAuthority('rpts')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<RecruitmentPipelineRes>> getAll(AtsPageQuery query) {
        return ApiResult.success(recruitmentPipelineService.getAll(query));
    }

    @PreAuthorize("hasAuthority('rpts')")
    @GetMapping("/owned")
    public ApiResult<AtsPageRsp<RecruitmentPipelineRes>> getAllForPlanOwner(AtsPageQuery query) {
        return ApiResult.success(recruitmentPipelineService.getAllForPlanOwner(query));
    }

    @PreAuthorize("hasAuthority('rpts')")
    @GetMapping("/shared")
    public ApiResult<AtsPageRsp<RecruitmentPipelineRes>> getAllSharedWithUser(AtsPageQuery query) {
        return ApiResult.success(recruitmentPipelineService.getAllSharedWithUser(query));
    }

    @PreAuthorize("hasAuthority('rpts')")
    @GetMapping("/listByJob")
    public ApiResult<List<RecruitmentPipelineRes>> listProcessTemplateByJob(@RequestParam(required = false) Long jobId) {
        throw new UnsupportedOperationException("Listing process templates by job is not implemented yet.");
        // return ApiResult.success(recruitmentPipelineService.listProcessTemplates()); //TODO: bug in original code, not returning results by jobId param
    }

    @PreAuthorize("hasAuthority('rpts')")
    @GetMapping("/templates")
    public ApiResult<AtsPageRsp<RecruitmentPipelineRes>> getAllSystemTemplates(AtsPageQuery query) {
        return ApiResult.success(recruitmentPipelineService.getAllSystemTemplates(query));
    }

    @PreAuthorize("hasAuthority('rptsa')")
    @PostMapping("/templates/create")
    public ApiResult<Map<String, String>> createTemplate(@RequestBody @Valid RecruitmentPipelineReq req) {
        return ApiResult.success(Collections.singletonMap("id", recruitmentPipelineService.createTemplate(req)));
    }

    @PreAuthorize("hasAuthority('rptsa')")
    @PostMapping("/templates/update")
    public ApiResult<Map<String, String>> updateTemplate(@RequestBody @Valid RecruitmentPipelineReq req) {
        return ApiResult.success(Collections.singletonMap("id", recruitmentPipelineService.updateTemplate(req)));
    }


}
