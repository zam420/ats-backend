package com.deepconnect.ats.job.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.job.service.JobPositionService;
import com.deepconnect.ats.jobapplication.dto.JobApplicationReq;
import com.deepconnect.ats.jobapplication.service.JobApplicationService;
import com.deepconnect.ats.job.dto.JobPositionSearchQuery;
import com.deepconnect.ats.job.dto.JobPositionSummaryResForCandidate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.job.dto.JobFiltersResForCandidate;
import com.deepconnect.ats.job.dto.JobPositionCandidateRes;
import com.deepconnect.ats.tenant.service.TenantLogoStorage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Validated
public class JobsController {

    private final JobPositionService jobPositionService;
    private final JobApplicationService jobApplicationService;

    @PreAuthorize("hasAuthority('jpsc')")
    @PostMapping(value = "/{id}/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<String> apply(@PathVariable @NotBlank String id,
            @RequestPart("application") @Valid JobApplicationReq request,
            @RequestPart("resume") MultipartFile resume) {
        return ApiResult.success(jobApplicationService.submit(id, request, resume));
    }

    @PreAuthorize("hasAuthority('jpsc')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<JobPositionSummaryResForCandidate>> findAll(@Valid JobPositionSearchQuery query) {
        return ApiResult.success(jobPositionService.findAllForCandidate(query));
    }

    @PreAuthorize("hasAuthority('jpsc')")
    @GetMapping("/{id}")
    public ApiResult<JobPositionCandidateRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(jobPositionService.findByPublicIdForCandidate(id));
    }

    @PreAuthorize("hasAuthority('jpsc')")
    @GetMapping("/filters")
    public ApiResult<JobFiltersResForCandidate> getFiltersForCandidates() throws Exception {
        return ApiResult.success(jobPositionService.getFiltersForCandidates());
    }

    // @PreAuthorize("hasAuthority('jps')")
    // @GetMapping("/listJobPostionForCV")
    // public ApiResult<List<JobPositionRes>> listJobPositionForCV() {
    //     return ApiResult.success(jobPositionService.listActive());
    // }

    // @PreAuthorize("hasAuthority('jpsc')")
    // @GetMapping("/listAll")
    // public ApiResult<AtsPageRsp<JobPosition>> listAll(AtsPageQuery query) {
    //     return ApiResult.success(jobPositionService.listJobPosition(query));
    // }

}
