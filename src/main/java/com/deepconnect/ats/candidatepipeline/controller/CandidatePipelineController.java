package com.deepconnect.ats.candidatepipeline.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deepconnect.ats.candidatepipeline.dto.CandidatePipelineRes;
import com.deepconnect.ats.candidatepipeline.dto.MoveCandidateReq;
import com.deepconnect.ats.candidatepipeline.dto.RejectCandidateReq;
import com.deepconnect.ats.candidatepipeline.service.CandidatePipelineService;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.job.dto.JobPositionCandidatePipelineRes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pipeline")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAuthority('jps')")
public class CandidatePipelineController {
    private final CandidatePipelineService service;

    @GetMapping("/jobs")
    public ApiResult<List<JobPositionCandidatePipelineRes>> jobs() {
        return ApiResult.success(service.jobs());
    }

    @GetMapping("/{jobId}")
    public ApiResult<CandidatePipelineRes> board(@PathVariable @NotBlank String jobId) {
        return ApiResult.success(service.board(jobId));
    }

    @PostMapping("/{jobId}/candidates/{candidateId}/move")
    public ApiResult<Boolean> move(@PathVariable @NotBlank String jobId,
            @PathVariable @NotBlank String candidateId, @RequestBody @Valid MoveCandidateReq request) {
        service.move(jobId, candidateId, request);
        return ApiResult.success(true);
    }

    @PostMapping("/{jobId}/candidates/{candidateId}/reject")
    public ApiResult<Boolean> reject(@PathVariable @NotBlank String jobId,
            @PathVariable @NotBlank String candidateId, @RequestBody @Valid RejectCandidateReq request) {
        service.reject(jobId, candidateId, request);
        return ApiResult.success(true);
    }
}
