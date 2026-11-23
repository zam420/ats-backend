package com.deepconnect.ats.job.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.job.service.JobPositionService;
import com.deepconnect.ats.job.dto.JobPositionSearchQuery;
import com.deepconnect.ats.job.dto.JobPositionSummaryResForCandidate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.job.dto.JobFiltersResForCandidate;
import com.deepconnect.ats.job.dto.JobPositionCandidateRes;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Validated
public class JobsController {

    private final JobPositionService jobPositionService;

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
