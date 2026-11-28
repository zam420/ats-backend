package com.deepconnect.ats.candidatepipeline.dto;

import java.time.Instant;
import java.util.List;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationFeedback;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.user.dto.UserSummary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

public final class CandidateEvaluationDto {
    private CandidateEvaluationDto() {}

    public record Round(String id, Integer order, String name, String description, EvaluationPlanRoundType roundType,
            List<String> interviewers, CandidateEvaluationStatus status, CandidateEvaluationResult result,
            Instant scheduledAt, Integer durationMinutes, Instant statusChangedAt, boolean canEnterFeedback,
            List<String> submittedInterviewers, List<SubmittedFeedback> submittedFeedback) {
        public static Round from(CandidateEvaluationRound round, boolean canEnterFeedback, List<SubmittedFeedback> submittedFeedback) {
            return new Round(round.getPublicId(), round.getOrder(), round.getName(), round.getDescription(),
                    round.getRoundType(), round.getInterviewers(), round.getStatus(), round.getResult(),
                    round.getScheduledAt(), round.getDurationMinutes(), round.getStatusChangedAt(), canEnterFeedback,
                    submittedFeedback.stream().map(SubmittedFeedback::interviewerId).toList(), submittedFeedback);
        }
    }

    public record SubmittedFeedback(String interviewerId, CandidateEvaluationResult result, Instant submittedAt) {
        public static SubmittedFeedback from(CandidateEvaluationFeedback feedback) {
            return new SubmittedFeedback(feedback.getInterviewerId(), feedback.getResult(), feedback.getSubmittedAt());
        }
    }

    public record Response(boolean initialized, CandidateEvaluationDecision decision, boolean canManage, boolean canDecide,
            List<Round> rounds, List<UserSummary> interviewers, String currentUserId) {}

    public record FeedbackRequest(CandidateEvaluationResult result, @Size(max = 1000) String overallFeedback,
            @Size(max = 1000) String strengths, @Size(max = 1000) String areasForImprovement) {}
    public record FeedbackResponse(CandidateEvaluationResult result, String overallFeedback, String strengths,
            String areasForImprovement, boolean submitted) {
        public static FeedbackResponse from(CandidateEvaluationFeedback feedback) {
            return feedback == null ? new FeedbackResponse(null, null, null, null, false)
                    : new FeedbackResponse(feedback.getResult(), feedback.getOverallFeedback(), feedback.getStrengths(),
                            feedback.getAreasForImprovement(), feedback.isSubmitted());
        }
    }

    public record ResultRequest(@NotNull CandidateEvaluationResult result) {}
    public record StatusRequest(@NotNull CandidateEvaluationStatus status) {}
    public record ScheduleRequest(@NotNull Instant scheduledAt, @NotNull @Positive Integer durationMinutes) {}
    public record InterviewersRequest(@NotNull List<@NotBlank String> interviewers) {}
    public record AddRoundRequest(@NotBlank @Size(max = 30) String name, @Size(max = 100) String description,
            @NotNull EvaluationPlanRoundType roundType, @NotNull List<@NotBlank String> interviewers) {}
    public record DecisionRequest(@NotNull CandidateEvaluationDecision decision) {}
}
