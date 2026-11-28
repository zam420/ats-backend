package com.deepconnect.ats.candidatepipeline.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationFeedback;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.jobapplication.model.JobApplication;

public final class CandidateEvaluationWorkflow {
    private CandidateEvaluationWorkflow() {}

    public static List<CandidateEvaluationRound> initialize(JobApplication application,
            List<JobEvaluationPlanRound> plan, List<CandidateEvaluationRound> existing) {
        if (application.getId() == null || application.getJobPositionId() == null || application.isClosed()
                || application.isEvaluationsInitialized() || existing == null || !existing.isEmpty()) {
            throw stale();
        }
        if (plan == null || plan.isEmpty()) {
            throw new InternalServerException("Cannot initialize evaluations without an evaluation plan");
        }
        List<JobEvaluationPlanRound> ordered = new ArrayList<>(plan);
        if (ordered.stream().anyMatch(round -> round == null || round.getOrder() == null || round.getOrder() < 0
                || round.getId() == null || !Objects.equals(round.getJobPositionId(), application.getJobPositionId())
                || round.getRoundType() == null || round.getName() == null || round.getName().isBlank())) {
            throw new InternalServerException("Invalid evaluation plan round");
        }
        ordered.sort(Comparator.comparing(JobEvaluationPlanRound::getOrder));
        Set<Integer> orders = new HashSet<>();
        Set<Long> planRoundIds = new HashSet<>();
        List<CandidateEvaluationRound> result = new ArrayList<>();
        for (JobEvaluationPlanRound template : ordered) {
            if (!orders.add(template.getOrder()) || !planRoundIds.add(template.getId())) {
                throw new InternalServerException("Duplicate evaluation plan round");
            }
            CandidateEvaluationRound round = new CandidateEvaluationRound();
            round.setJobApplicationId(application.getId());
            round.setPlanRoundId(template.getId());
            round.setOrder(result.size());
            round.setName(template.getName());
            round.setDescription(template.getDescription());
            round.setRoundType(template.getRoundType());
            round.setInterviewers(template.getInterviewers() == null ? null : new ArrayList<>(template.getInterviewers()));
            result.add(round);
        }
        application.setEvaluationsInitialized(true);
        application.setEvaluationDecision(null);
        return result;
    }

    public static CandidateEvaluationRound currentRound(JobApplication application, List<CandidateEvaluationRound> rounds) {
        validateRounds(application, rounds);
        return rounds.stream()
                .filter(round -> round.getStatus() != CandidateEvaluationStatus.COMPLETED)
                .min(Comparator.comparing(CandidateEvaluationRound::getOrder))
                .orElse(null);
    }

    public static CandidateEvaluationRound recordResult(JobApplication application, List<CandidateEvaluationRound> rounds,
            String roundId, CandidateEvaluationResult result) {
        if (result == null || application.getEvaluationDecision() != null) throw stale();
        CandidateEvaluationRound current = requireCurrent(application, rounds, roundId);
        current.setStatusChangedAt(Instant.now());
        current.setStatus(CandidateEvaluationStatus.COMPLETED);
        current.setResult(result);
        return current;
    }

    public static CandidateEvaluationRound changeStatus(JobApplication application, List<CandidateEvaluationRound> rounds,
            String roundId, CandidateEvaluationStatus status) {
        if (status == null || status == CandidateEvaluationStatus.COMPLETED
                || status == CandidateEvaluationStatus.SCHEDULED || application.getEvaluationDecision() != null) {
            throw stale();
        }
        CandidateEvaluationRound current = requireCurrent(application, rounds, roundId);
        if (current.getStatus() != status) current.setStatusChangedAt(Instant.now());
        current.setStatus(status);
        return current;
    }

    public static CandidateEvaluationRound schedule(JobApplication application, List<CandidateEvaluationRound> rounds,
            String roundId, Instant scheduledAt) {
        if (scheduledAt == null || application.getEvaluationDecision() != null) throw stale();
        CandidateEvaluationRound current = requireCurrent(application, rounds, roundId);
        current.setScheduledAt(scheduledAt);
        if (current.getStatus() != CandidateEvaluationStatus.SCHEDULED) current.setStatusChangedAt(Instant.now());
        current.setStatus(CandidateEvaluationStatus.SCHEDULED);
        return current;
    }

    public static CandidateEvaluationRound requireFeedbackRound(JobApplication application,
            List<CandidateEvaluationRound> rounds, String roundId, String interviewerId) {
        if (application.getEvaluationDecision() != null || interviewerId == null) throw stale();
        CandidateEvaluationRound current = requireCurrent(application, rounds, roundId);
        if (!isFeedbackStatus(current.getStatus()) || current.getInterviewers() == null
                || !current.getInterviewers().contains(interviewerId)) {
            throw stale();
        }
        return current;
    }

    public static boolean isFeedbackStatus(CandidateEvaluationStatus status) {
        return status == CandidateEvaluationStatus.AWAITING_FEEDBACK;
    }

    /**
     * Completes the round once every current interviewer has submitted. The round result is the average of the
     * interviewers' ratings (Strong Yes 2, Yes 1, No -1, Strong No -2); an average of 1.5 or more is Strong Yes,
     * above 0 is Yes, down to above -1.5 is No, otherwise Strong No.
     */
    public static boolean completeIfAllSubmitted(CandidateEvaluationRound round, List<CandidateEvaluationFeedback> feedbacks) {
        List<String> interviewers = round.getInterviewers();
        if (interviewers == null || interviewers.isEmpty()) return false;
        List<CandidateEvaluationFeedback> submitted = new ArrayList<>();
        for (String interviewer : interviewers) {
            CandidateEvaluationFeedback feedback = feedbacks.stream()
                    .filter(item -> item.isSubmitted() && interviewer.equals(item.getInterviewerId())
                            && item.getResult() != null && item.getResult() != CandidateEvaluationResult.CANCELLED)
                    .findFirst().orElse(null);
            if (feedback == null) return false;
            submitted.add(feedback);
        }
        double average = submitted.stream().mapToInt(item -> weight(item.getResult())).average().orElseThrow();
        round.setResult(average >= 1.5 ? CandidateEvaluationResult.STRONG_YES
                : average > 0 ? CandidateEvaluationResult.YES
                : average > -1.5 ? CandidateEvaluationResult.NO : CandidateEvaluationResult.STRONG_NO);
        round.setStatus(CandidateEvaluationStatus.COMPLETED);
        round.setStatusChangedAt(Instant.now());
        return true;
    }

    private static int weight(CandidateEvaluationResult result) {
        return switch (result) {
            case STRONG_YES -> 2;
            case YES -> 1;
            case NO -> -1;
            case STRONG_NO -> -2;
            default -> throw stale();
        };
    }

    public static void decide(JobApplication application, List<CandidateEvaluationRound> rounds,
            CandidateEvaluationDecision decision) {
        if (decision == null || application.getEvaluationDecision() != null || currentRound(application, rounds) != null) {
            throw stale();
        }
        application.setEvaluationDecision(decision);
    }

    public static CandidateEvaluationRound addRound(JobApplication application, List<CandidateEvaluationRound> rounds,
            String name, String description, EvaluationPlanRoundType type, List<String> interviewers) {
        validateRounds(application, rounds);
        if (application.getEvaluationDecision() != null || name == null || name.isBlank()
                || name.length() > 30 || (description != null && description.length() > 100) || type == null) {
            throw stale();
        }
        List<String> checkedInterviewers = copyInterviewers(interviewers);
        int maxOrder = rounds.stream().mapToInt(CandidateEvaluationRound::getOrder).max().orElse(-1);
        if (maxOrder == Integer.MAX_VALUE) throw new InternalServerException("Too many candidate evaluation rounds");
        CandidateEvaluationRound round = new CandidateEvaluationRound();
        round.setJobApplicationId(application.getId());
        round.setOrder(maxOrder + 1);
        round.setName(name.trim());
        round.setDescription(description);
        round.setRoundType(type);
        round.setInterviewers(checkedInterviewers);
        return round;
    }

    public static CandidateEvaluationRound changeInterviewers(JobApplication application, List<CandidateEvaluationRound> rounds,
            String roundId, List<String> interviewers) {
        validateRounds(application, rounds);
        if (application.getEvaluationDecision() != null || roundId == null) throw stale();
        CandidateEvaluationRound round = requireCurrent(application, rounds, roundId);
        if (round.getStatus() != CandidateEvaluationStatus.PENDING
                && round.getStatus() != CandidateEvaluationStatus.SCHEDULED) throw stale();
        round.setInterviewers(copyInterviewers(interviewers));
        return round;
    }

    private static List<String> copyInterviewers(List<String> interviewers) {
        if (interviewers == null || interviewers.stream().anyMatch(id -> id == null || id.isBlank())
                || new HashSet<>(interviewers).size() != interviewers.size()) {
            throw stale();
        }
        return new ArrayList<>(interviewers);
    }

    private static CandidateEvaluationRound requireCurrent(JobApplication application, List<CandidateEvaluationRound> rounds,
            String roundId) {
        CandidateEvaluationRound current = currentRound(application, rounds);
        if (current == null || roundId == null || !roundId.equals(current.getPublicId())) throw stale();
        return current;
    }

    private static void validateRounds(JobApplication application, List<CandidateEvaluationRound> rounds) {
        if (application.getId() == null || application.isClosed() || !application.isEvaluationsInitialized() || rounds == null) {
            throw stale();
        }
        Set<Integer> orders = new HashSet<>();
        for (CandidateEvaluationRound round : rounds) {
            if (round == null || !Objects.equals(round.getJobApplicationId(), application.getId())
                    || round.getOrder() == null || round.getOrder() < 0 || !orders.add(round.getOrder())
                    || round.getStatus() == null || !round.isResultConsistent()) {
                throw new InternalServerException("Invalid candidate evaluation rounds");
            }
        }
    }

    private static CustomException stale() {
        return new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
    }
}
