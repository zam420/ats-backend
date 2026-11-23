package com.deepconnect.ats.jobapplication.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.jobapplication.dto.JobApplicationReq;
import com.deepconnect.ats.jobapplication.dto.JobApplicationSearchQuery;
import com.deepconnect.ats.jobapplication.dto.JobApplicationSummaryRes;
import com.deepconnect.ats.jobapplication.service.JobApplicationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Validated
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PreAuthorize("hasAuthority('jpsc')")
    @PostMapping(value = "/submit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<String> apply(@PathVariable @NotBlank String id,
            @RequestPart("application") @Valid JobApplicationReq request,
            @RequestPart("resume") MultipartFile resume) {
        return ApiResult.success(jobApplicationService.submit(id, request, resume));
    }

    @PreAuthorize("hasAuthority('jpsc')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<JobApplicationSummaryRes>> findAll(@Valid JobApplicationSearchQuery query) {
        return ApiResult.success(jobApplicationService.findAllForCandidate(query));
    }


}
