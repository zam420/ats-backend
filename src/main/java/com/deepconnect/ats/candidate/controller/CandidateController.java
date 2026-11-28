package com.deepconnect.ats.candidate.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.deepconnect.ats.candidate.dto.CandidateResMin;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.candidate.service.CandidateService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
@Validated
public class CandidateController {

    private final CandidateService candidateService;

    @PreAuthorize("hasAuthority('jps')")
    @GetMapping("/list/{jobId}")
    public ApiResult<List<CandidateResMin>> findAllAsList(@PathVariable String jobId) {
        return ApiResult.success(candidateService.findAllAsList(jobId));
    }

}
