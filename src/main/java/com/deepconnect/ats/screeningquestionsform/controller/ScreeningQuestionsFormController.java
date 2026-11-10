package com.deepconnect.ats.screeningquestionsform.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormReq;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionsFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormRes;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormDetailsRes;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sqform")
@RequiredArgsConstructor
@Validated // 1. Required at the class level for path variable/request param validation
public class ScreeningQuestionsFormController {

    private final ScreeningQuestionsFormService screeningQuestionTemplateService;

    @PreAuthorize("hasAuthority('sqtc')")
    @PostMapping("/create")
    public ApiResult<Map<String, String>> create(@RequestBody @Valid ScreeningQuestionsFormReq req) {
        return ApiResult.success(Collections.singletonMap("id", screeningQuestionTemplateService.create(req)));
    }

    @PreAuthorize("hasAuthority('sqtc')")
    @PostMapping("/saveas")
    public ApiResult<Map<String, String>> saveAs(@RequestBody @Valid ScreeningQuestionsFormReq req) {
        return ApiResult.success(Collections.singletonMap("id", screeningQuestionTemplateService.create(req)));
    }

    @PreAuthorize("hasAuthority('sqte')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody @Valid ScreeningQuestionsFormReq req) {
        return ApiResult.success(Collections.singletonMap("id", screeningQuestionTemplateService.update(req)));
    }

    @PreAuthorize("hasAuthority('sqte')")
    @PostMapping("/deactivate/{id}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        return ApiResult.success(screeningQuestionTemplateService.deactivate(id));
    }

    @PreAuthorize("hasAuthority('sqts')")
    @GetMapping("/roundtypes")
    public ApiResult<List<ScreeningQuestionType>> findAllScreeningQuestionTypes() throws Exception {
        return ApiResult.success(screeningQuestionTemplateService.findAllScreeningQuestionTypes());
    }

    @PreAuthorize("hasAuthority('sqts')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<ScreeningQuestionsFormRes>> findAll(AtsPageQuery query) {
        return ApiResult.success(screeningQuestionTemplateService.findAll(query));
    }

    @PreAuthorize("hasAuthority('sqts')")
    @GetMapping("/{id}")
    public ApiResult<ScreeningQuestionsFormDetailsRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(screeningQuestionTemplateService.findByPublicIdForTenant(id));
    }

    /**
     * Get all screening question templates for the current tenant as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @param query
     * @return
     */
    @PreAuthorize("hasAuthority('sqts')")
    @GetMapping("/list")
    public ApiResult<List<ScreeningQuestionsFormRes>> findAllAsList() {
        return ApiResult.success(screeningQuestionTemplateService.findAllAsList());
    }

    @PreAuthorize("hasAuthority('sqts')")
    @GetMapping("/owned")
    public ApiResult<AtsPageRsp<ScreeningQuestionsFormRes>> findAllForPlanOwner(AtsPageQuery query) {
        return ApiResult.success(screeningQuestionTemplateService.findAllForPlanOwner(query));
    }

    @PreAuthorize("hasAuthority('sqts')")
    @GetMapping("/shared")
    public ApiResult<AtsPageRsp<ScreeningQuestionsFormRes>> findAllSharedWithUser(AtsPageQuery query) {
        return ApiResult.success(screeningQuestionTemplateService.findAllSharedWithUser(query));
    }

    @PreAuthorize("hasAuthority('sqts')")
    @GetMapping("/templates")
    public ApiResult<AtsPageRsp<ScreeningQuestionsFormRes>> findAllSystemTemplates(AtsPageQuery query) {
        return ApiResult.success(screeningQuestionTemplateService.findAllSystemTemplates(query));
    }

    @PreAuthorize("hasAuthority('sqtsa')")
    @PostMapping("/templates/create")
    public ApiResult<Map<String, String>> createSystemTemplate(@RequestBody @Valid ScreeningQuestionsFormReq req) {
        return ApiResult.success(Collections.singletonMap("id", screeningQuestionTemplateService.createSystemTemplate(req)));
    }

    @PreAuthorize("hasAuthority('sqtsa')")
    @PostMapping("/templates/update")
    public ApiResult<Map<String, String>> updateSystemTemplate(@RequestBody @Valid ScreeningQuestionsFormReq req) {
        return ApiResult.success(Collections.singletonMap("id", screeningQuestionTemplateService.updateSystemTemplate(req)));
    }

    @PreAuthorize("hasAuthority('sqts')")
    @GetMapping("/listByJob")
    public ApiResult<List<ScreeningQuestionsFormRes>> listProcessTemplateByJob(@RequestParam(required = false) Long jobId) {
        throw new UnsupportedOperationException("Listing process templates by job is not implemented yet.");
        // return ApiResult.success(screeningQuestionTemplateService.listProcessTemplates()); //TODO: bug in original code, not returning results by jobId param
    }

}
