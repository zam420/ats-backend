package com.deepconnect.ats.interviewprocess.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.deepconnect.ats.interviewprocess.dto.InterviewProcessReq;
import com.deepconnect.ats.interviewprocess.service.InterviewProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQueryWithStatus;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.interviewprocess.dto.InterviewProcessRes;
import com.deepconnect.ats.interviewprocess.dto.InterviewProcessDetailsRes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviewprocess")
@RequiredArgsConstructor
@Validated // 1. Required at the class level for path variable/request param validation
public class InterviewProcessController {

    private final InterviewProcessService interviewProcessService;

    @PreAuthorize("hasAuthority('iptc')")
    @PostMapping("/create")
    public ApiResult<Map<String, String>> create(@RequestBody InterviewProcessReq req) {
        String id = interviewProcessService.create(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody InterviewProcessReq req) {
        String id = interviewProcessService.update(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('ipte')")
    @PostMapping("/deactivate/{id}/{status}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id, 
        @PathVariable @NotNull(message = "{field.common.status}{validation.notBlank}") Integer status) {
        interviewProcessService.deactivate(id, status);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/{id}")
    public ApiResult<InterviewProcessDetailsRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(interviewProcessService.findByPublicId(id).orElse(null));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<InterviewProcessRes>> getAllByPage(AtsPageQueryWithStatus query) {
        return ApiResult.success(interviewProcessService.getAllByPage(query));
    }

    @PreAuthorize("hasAuthority('ipts')")
    @GetMapping("/listByJob")
    public ApiResult<List<InterviewProcessRes>> listProcessTemplateByJob(@RequestParam(required = false) Long jobId) {
        throw new UnsupportedOperationException("Listing process templates by job is not implemented yet.");
        // return ApiResult.success(interviewProcessService.listProcessTemplates()); //TODO: bug in original code, not returning results by jobId param
    }

}
