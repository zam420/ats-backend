package com.deepconnect.ats.candidatepipeline.dto;

import java.time.Instant;
import java.util.List;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.user.dto.UserSummary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class CandidateEvaluationDto {
    private CandidateEvaluationDto() {}

    public record Round(String id, Integer order, String name, String description, EvaluationPlanRoundType roundType,
            List<String> interviewers, CandidateEvaluationStatus status, CandidateEvaluationResult result,
            Instant scheduledAt) {
        public static Round from(CandidateEvaluationRound round) {
            return new Round(round.getPublicId(), round.getOrder(), round.getName(), round.getDescription(),
                    round.getRoundType(), round.getInterviewers(), round.getStatus(), round.getResult(),
                    round.getScheduledAt());
        }
    }

    public record Response(boolean initialized, CandidateEvaluationDecision decision, boolean canManage, boolean canDecide,
            List<Round> rounds, List<UserSummary> interviewers) {}

    public record ResultRequest(@NotNull CandidateEvaluationResult result) {}
    public record StatusRequest(@NotNull CandidateEvaluationStatus status) {}
    public record ScheduleRequest(@NotNull Instant scheduledAt, @NotNull List<@NotBlank String> interviewers) {}
    public record InterviewersRequest(@NotNull List<@NotBlank String> interviewers) {}
    public record AddRoundRequest(@NotBlank @Size(max = 30) String name, @Size(max = 100) String description,
            @NotNull EvaluationPlanRoundType roundType, @NotNull List<@NotBlank String> interviewers) {}
    public record DecisionRequest(@NotNull CandidateEvaluationDecision decision) {}
}
