package com.deepconnect.ats.candidatepipeline.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deepconnect.ats.candidatepipeline.dto.CandidateApplicationRes;
import com.deepconnect.ats.candidatepipeline.service.CandidateApplicationService;
import com.deepconnect.ats.dto.ApiResult;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pipeline/{jobId}/candidates/{candidateId}")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAuthority('jps')")
public class CandidateApplicationController {
    private final CandidateApplicationService service;

    @GetMapping
    public ApiResult<CandidateApplicationRes> get(@PathVariable @NotBlank String jobId,
            @PathVariable @NotBlank String candidateId) {
        return ApiResult.success(service.get(jobId, candidateId));
    }

    @GetMapping("/resume")
    public ResponseEntity<FileSystemResource> resume(@PathVariable @NotBlank String jobId,
            @PathVariable @NotBlank String candidateId) {
        var resume = service.resume(jobId, candidateId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(resume.filename(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(new FileSystemResource(resume.path()));
    }
}
