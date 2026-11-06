package com.deepconnect.ats.evaluationplan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanReq;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRes;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanDetailsRes;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluationplan")
@RequiredArgsConstructor
@Validated // 1. Required at the class level for path variable/request param validation
public class EvaluationPlanController {

    private final EvaluationPlanService evaluationPlanService;

    @PreAuthorize("hasAuthority('iptc')")
    @PostMapping("/create")
    public ApiResult<Map<String, String>> create(@RequestBody @Valid EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.create(req)));
    }

    @PreAuthorize("hasAuthority('iptc')")
    @PostMapping("/saveas")
    public ApiResult<Map<String, String>> saveAs(@RequestBody @Valid EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.create(req)));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody @Valid EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.update(req)));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/deactivate/{id}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        return ApiResult.success(evaluationPlanService.deactivate(id));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/roundtypes")
    public ApiResult<List<EvaluationPlanRoundType>> findAllRoundTypes() throws Exception {
        return ApiResult.success(evaluationPlanService.findAllRoundTypes());
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<EvaluationPlanRes>> findAll(AtsPageQuery query) {
        return ApiResult.success(evaluationPlanService.findAll(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/{id}")
    public ApiResult<EvaluationPlanDetailsRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(evaluationPlanService.findByPublicIdForTenant(id));
    }

    /**
     * Get all interview plans for the current tenant as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @param query
     * @return
     */
    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/list")
    public ApiResult<List<EvaluationPlanRes>> findAllAsList() {
        return ApiResult.success(evaluationPlanService.findAllAsList());
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/owned")
    public ApiResult<AtsPageRsp<EvaluationPlanRes>> findAllForPlanOwner(AtsPageQuery query) {
        return ApiResult.success(evaluationPlanService.findAllForPlanOwner(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/shared")
    public ApiResult<AtsPageRsp<EvaluationPlanRes>> findAllSharedWithUser(AtsPageQuery query) {
        return ApiResult.success(evaluationPlanService.findAllSharedWithUser(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/templates")
    public ApiResult<AtsPageRsp<EvaluationPlanRes>> findAllSystemTemplates(AtsPageQuery query) {
        return ApiResult.success(evaluationPlanService.findAllSystemTemplates(query));
    }

    @PreAuthorize("hasAuthority('iptsa')")
    @PostMapping("/templates/create")
    public ApiResult<Map<String, String>> createTemplate(@RequestBody @Valid EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.createTemplate(req)));
    }

    @PreAuthorize("hasAuthority('iptsa')")
    @PostMapping("/templates/update")
    public ApiResult<Map<String, String>> updateTemplate(@RequestBody @Valid EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.updateTemplate(req)));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/listByJob")
    public ApiResult<List<EvaluationPlanRes>> listProcessTemplateByJob(@RequestParam(required = false) Long jobId) {
        throw new UnsupportedOperationException("Listing process templates by job is not implemented yet.");
        // return ApiResult.success(evaluationPlanService.listProcessTemplates()); //TODO: bug in original code, not returning results by jobId param
    }

}
