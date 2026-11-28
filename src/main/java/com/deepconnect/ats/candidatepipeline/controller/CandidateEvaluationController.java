package com.deepconnect.ats.candidatepipeline.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deepconnect.ats.candidatepipeline.dto.CandidateEvaluationDto;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationService;
import com.deepconnect.ats.dto.ApiResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pipeline/{jobId}/candidate/{candidateId}/evaluation")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAuthority('jps')")
public class CandidateEvaluationController {
    private final CandidateEvaluationService service;

    @GetMapping
    public ApiResult<CandidateEvaluationDto.Response> get(@PathVariable @NotBlank String jobId,
            @PathVariable @NotBlank String candidateId) {
        return ApiResult.success(service.get(jobId, candidateId));
    }

    @PostMapping("/rounds")
    public ApiResult<Boolean> addRound(@PathVariable @NotBlank String jobId, @PathVariable @NotBlank String candidateId,
            @RequestBody @Valid CandidateEvaluationDto.AddRoundRequest request) {
        service.addRound(jobId, candidateId, request.name(), request.description(), request.roundType(), request.interviewers());
        return ApiResult.success(true);
    }

    @PostMapping("/rounds/{roundId}/result")
    public ApiResult<Boolean> result(@PathVariable @NotBlank String jobId, @PathVariable @NotBlank String candidateId,
            @PathVariable @NotBlank String roundId, @RequestBody @Valid CandidateEvaluationDto.ResultRequest request) {
        service.recordResult(jobId, candidateId, roundId, request.result());
        return ApiResult.success(true);
    }

    @GetMapping("/rounds/{roundId}/feedback")
    public ApiResult<CandidateEvaluationDto.FeedbackResponse> feedback(@PathVariable @NotBlank String jobId,
            @PathVariable @NotBlank String candidateId, @PathVariable @NotBlank String roundId) {
        return ApiResult.success(service.getFeedback(jobId, candidateId, roundId));
    }

    @GetMapping("/rounds/{roundId}/feedback/{interviewerId}")
    public ApiResult<CandidateEvaluationDto.FeedbackResponse> submittedFeedback(@PathVariable @NotBlank String jobId,
            @PathVariable @NotBlank String candidateId, @PathVariable @NotBlank String roundId,
            @PathVariable @NotBlank String interviewerId) {
        return ApiResult.success(service.getSubmittedFeedback(jobId, candidateId, roundId, interviewerId));
    }

    @PostMapping("/rounds/{roundId}/feedback/draft")
    public ApiResult<Boolean> saveFeedbackDraft(@PathVariable @NotBlank String jobId, @PathVariable @NotBlank String candidateId,
            @PathVariable @NotBlank String roundId, @RequestBody @Valid CandidateEvaluationDto.FeedbackRequest request) {
        service.saveFeedback(jobId, candidateId, roundId, request, false);
        return ApiResult.success(true);
    }

    @PostMapping("/rounds/{roundId}/feedback/submit")
    public ApiResult<Boolean> submitFeedback(@PathVariable @NotBlank String jobId, @PathVariable @NotBlank String candidateId,
            @PathVariable @NotBlank String roundId, @RequestBody @Valid CandidateEvaluationDto.FeedbackRequest request) {
        service.saveFeedback(jobId, candidateId, roundId, request, true);
        return ApiResult.success(true);
    }

    @PostMapping("/rounds/{roundId}/status")
    public ApiResult<Boolean> status(@PathVariable @NotBlank String jobId, @PathVariable @NotBlank String candidateId,
            @PathVariable @NotBlank String roundId, @RequestBody @Valid CandidateEvaluationDto.StatusRequest request) {
        service.changeStatus(jobId, candidateId, roundId, request.status());
        return ApiResult.success(true);
    }

    @PostMapping("/rounds/{roundId}/schedule")
    public ApiResult<Boolean> schedule(@PathVariable @NotBlank String jobId, @PathVariable @NotBlank String candidateId,
            @PathVariable @NotBlank String roundId, @RequestBody @Valid CandidateEvaluationDto.ScheduleRequest request) {
        service.schedule(jobId, candidateId, roundId, request.scheduledAt(), request.durationMinutes());
        return ApiResult.success(true);
    }

    @PostMapping("/rounds/{roundId}/interviewers")
    public ApiResult<Boolean> interviewers(@PathVariable @NotBlank String jobId, @PathVariable @NotBlank String candidateId,
            @PathVariable @NotBlank String roundId, @RequestBody @Valid CandidateEvaluationDto.InterviewersRequest request) {
        service.changeInterviewers(jobId, candidateId, roundId, request.interviewers());
        return ApiResult.success(true);
    }

    @PostMapping("/decision")
    public ApiResult<Boolean> decision(@PathVariable @NotBlank String jobId, @PathVariable @NotBlank String candidateId,
            @RequestBody @Valid CandidateEvaluationDto.DecisionRequest request) {
        service.decide(jobId, candidateId, request.decision());
        return ApiResult.success(true);
    }
}
