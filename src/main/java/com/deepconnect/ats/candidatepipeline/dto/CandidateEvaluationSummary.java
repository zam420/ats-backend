package com.deepconnect.ats.candidatepipeline.dto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationWorkflow;
import com.deepconnect.ats.jobapplication.model.JobApplication;

public record CandidateEvaluationSummary(String currentRoundName, CandidateEvaluationStatus currentStatus,
        List<String> currentOwners, long completedRounds, int totalRounds,
        long strongYes, long yes, long no, long strongNo, long cancelled,
        List<RoundResult> roundResults, CandidateEvaluationDecision decision) {
    public record RoundResult(String name, CandidateEvaluationResult result) {}

    public static CandidateEvaluationSummary from(JobApplication application, List<CandidateEvaluationRound> rounds,
            String jobOwner, Map<String, String> interviewerNames) {
        CandidateEvaluationRound current = CandidateEvaluationWorkflow.currentRound(application, rounds);
        CandidateEvaluationRound displayed = current != null ? current : rounds.stream()
                .max(Comparator.comparing(CandidateEvaluationRound::getOrder)).orElse(null);
        List<String> owners = current == null ? List.of()
                : current.getStatus() == CandidateEvaluationStatus.PENDING
                    ? (jobOwner == null || jobOwner.isBlank() ? List.of() : List.of(jobOwner))
                    : current.getInterviewers() == null ? List.of()
                    : current.getInterviewers().stream().map(id -> interviewerNames.getOrDefault(id, id)).toList();
        return new CandidateEvaluationSummary(displayed == null ? null : displayed.getName(),
                current == null ? CandidateEvaluationStatus.COMPLETED : current.getStatus(),
                owners,
                rounds.stream().filter(round -> round.getStatus() == CandidateEvaluationStatus.COMPLETED).count(),
                rounds.size(), count(rounds, CandidateEvaluationResult.STRONG_YES),
                count(rounds, CandidateEvaluationResult.YES), count(rounds, CandidateEvaluationResult.NO),
                count(rounds, CandidateEvaluationResult.STRONG_NO), count(rounds, CandidateEvaluationResult.CANCELLED),
                rounds.stream().sorted(Comparator.comparing(CandidateEvaluationRound::getOrder))
                        .map(round -> new RoundResult(round.getName(), round.getResult())).toList(),
                application.getEvaluationDecision());
    }

    private static long count(List<CandidateEvaluationRound> rounds, CandidateEvaluationResult result) {
        return rounds.stream().filter(round -> round.getResult() == result).count();
    }
}
