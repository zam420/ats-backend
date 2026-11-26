package com.deepconnect.ats.candidatepipeline.dto;

import java.util.Comparator;
import java.util.List;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationWorkflow;
import com.deepconnect.ats.jobapplication.model.JobApplication;

public record CandidateEvaluationSummary(String currentRoundName, CandidateEvaluationStatus currentStatus,
        long completedRounds, int totalRounds, long passed, long failed, long cancelled,
        CandidateEvaluationDecision decision) {
    public static CandidateEvaluationSummary from(JobApplication application, List<CandidateEvaluationRound> rounds) {
        CandidateEvaluationRound current = CandidateEvaluationWorkflow.currentRound(application, rounds);
        CandidateEvaluationRound displayed = current != null ? current : rounds.stream()
                .max(Comparator.comparing(CandidateEvaluationRound::getOrder)).orElse(null);
        return new CandidateEvaluationSummary(displayed == null ? null : displayed.getName(),
                current == null ? CandidateEvaluationStatus.COMPLETED : current.getStatus(),
                rounds.stream().filter(round -> round.getStatus() == CandidateEvaluationStatus.COMPLETED).count(),
                rounds.size(), count(rounds, CandidateEvaluationResult.PASSED),
                count(rounds, CandidateEvaluationResult.FAILED), count(rounds, CandidateEvaluationResult.CANCELLED),
                application.getEvaluationDecision());
    }

    private static long count(List<CandidateEvaluationRound> rounds, CandidateEvaluationResult result) {
        return rounds.stream().filter(round -> round.getResult() == result).count();
    }
}
