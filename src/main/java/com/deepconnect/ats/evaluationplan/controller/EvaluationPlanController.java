package com.deepconnect.ats.evaluationplan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
    public ApiResult<Map<String, String>> create(@RequestBody EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.create(req)));
    }

    @PreAuthorize("hasAuthority('iptc')")
    @PostMapping("/saveas")
    public ApiResult<Map<String, String>> saveAs(@RequestBody EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.create(req)));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.update(req)));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/deactivate/{id}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        return ApiResult.success(evaluationPlanService.deactivate(id));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/{id}")
    public ApiResult<EvaluationPlanDetailsRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(evaluationPlanService.findByPublicIdForTenant(id));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/roundtypes")
    public ApiResult<List<EvaluationPlanRoundType>> getAllRoundTypes() throws Exception {
        return ApiResult.success(evaluationPlanService.getAllRoundTypes());
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<EvaluationPlanRes>> getAll(AtsPageQuery query) {
        return ApiResult.success(evaluationPlanService.getAll(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/owned")
    public ApiResult<AtsPageRsp<EvaluationPlanRes>> getAllForPlanOwner(AtsPageQuery query) {
        return ApiResult.success(evaluationPlanService.getAllForPlanOwner(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/shared")
    public ApiResult<AtsPageRsp<EvaluationPlanRes>> getAllSharedWithUser(AtsPageQuery query) {
        return ApiResult.success(evaluationPlanService.getAllSharedWithUser(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/templates")
    public ApiResult<AtsPageRsp<EvaluationPlanRes>> getAllSystemTemplates(AtsPageQuery query) {
        return ApiResult.success(evaluationPlanService.getAllSystemTemplates(query));
    }

    @PreAuthorize("hasAuthority('iptsa')")
    @PostMapping("/templates/create")
    public ApiResult<Map<String, String>> createTemplate(@RequestBody EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.createTemplate(req)));
    }

    @PreAuthorize("hasAuthority('iptsa')")
    @PostMapping("/templates/update")
    public ApiResult<Map<String, String>> updateTemplate(@RequestBody EvaluationPlanReq req) {
        return ApiResult.success(Collections.singletonMap("id", evaluationPlanService.updateTemplate(req)));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/listByJob")
    public ApiResult<List<EvaluationPlanRes>> listProcessTemplateByJob(@RequestParam(required = false) Long jobId) {
        throw new UnsupportedOperationException("Listing process templates by job is not implemented yet.");
        // return ApiResult.success(evaluationPlanService.listProcessTemplates()); //TODO: bug in original code, not returning results by jobId param
    }

}
