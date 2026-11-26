package com.deepconnect.ats.candidatepipeline.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.jobapplication.model.JobApplication;

public final class CandidateEvaluationPlanSync {
    private CandidateEvaluationPlanSync() {}

    public record Result(List<CandidateEvaluationRound> rounds, List<CandidateEvaluationRound> removed,
            boolean hasIgnoredChanges) {}

    public static Result synchronize(JobApplication application, List<CandidateEvaluationRound> candidateRounds,
            List<JobEvaluationPlanRound> previousPlan, List<JobEvaluationPlanRound> updatedPlan) {
        CandidateEvaluationRound current = CandidateEvaluationWorkflow.currentRound(application, candidateRounds);
        List<JobEvaluationPlanRound> previous = orderedPlan(previousPlan, application.getJobPositionId());
        List<JobEvaluationPlanRound> updated = orderedPlan(updatedPlan, application.getJobPositionId());
        Map<String, JobEvaluationPlanRound> oldByLineage = byLineage(previous);
        Map<String, JobEvaluationPlanRound> newByLineage = byLineage(updated);
        Map<String, Integer> newPositions = new HashMap<>();
        for (int i = 0; i < updated.size(); i++) newPositions.put(updated.get(i).getLineageId(), i);

        List<CandidateEvaluationRound> ordered = new ArrayList<>(candidateRounds);
        ordered.sort(Comparator.comparing(CandidateEvaluationRound::getOrder));
        if (current == null) {
            return new Result(List.copyOf(ordered), List.of(), planChanged(previous, updated));
        }

        List<CandidateEvaluationRound> removed = new ArrayList<>();
        List<CandidateEvaluationRound> prefix = new ArrayList<>();
        List<CandidateEvaluationRound> pendingCustom = new ArrayList<>();
        Map<String, CandidateEvaluationRound> pendingByLineage = new HashMap<>();
        Set<String> progressed = new HashSet<>();
        boolean ignored = false;
        boolean reachedCurrent = false;
        for (CandidateEvaluationRound round : ordered) {
            if (round == current) reachedCurrent = true;
            if (round.getStatus() != CandidateEvaluationStatus.PENDING) {
                if (reachedCurrent && round != current) {
                    throw new InternalServerException("Candidate evaluation rounds are out of sequence");
                }
                prefix.add(round);
                if (round.getPlanLineageId() != null) {
                    progressed.add(round.getPlanLineageId());
                    ignored |= changed(oldByLineage.get(round.getPlanLineageId()), newByLineage.get(round.getPlanLineageId()));
                }
            } else if (round.getPlanLineageId() == null) {
                if (round == current) prefix.add(round);
                else pendingCustom.add(round);
            } else if (!newByLineage.containsKey(round.getPlanLineageId())) {
                removed.add(round);
            } else if (pendingByLineage.put(round.getPlanLineageId(), round) != null) {
                throw new InternalServerException("Duplicate candidate evaluation lineage");
            }
        }

        CandidateEvaluationRound anchor = current;
        if (removed.contains(current)) {
            anchor = ordered.stream().filter(round -> round.getOrder() > current.getOrder())
                    .filter(round -> round.getStatus() == CandidateEvaluationStatus.PENDING)
                    .filter(round -> round.getPlanLineageId() == null || newByLineage.containsKey(round.getPlanLineageId()))
                    .findFirst().orElse(null);
            if (anchor == null) {
                for (CandidateEvaluationRound round : ordered) {
                    if (round.getStatus() == CandidateEvaluationStatus.PENDING && !removed.contains(round)) removed.add(round);
                }
                boolean ignoredAdditions = updated.stream().anyMatch(round -> !oldByLineage.containsKey(round.getLineageId()));
                return new Result(List.copyOf(prefix), List.copyOf(removed), ignored || ignoredAdditions);
            }
            if (anchor.getPlanLineageId() == null) {
                pendingCustom.remove(anchor);
                prefix.add(anchor);
            }
        }

        int boundary;
        Integer anchorPosition = anchor.getPlanLineageId() == null ? null : newPositions.get(anchor.getPlanLineageId());
        if (anchorPosition != null) {
            boundary = anchorPosition + (anchor.getStatus() == CandidateEvaluationStatus.PENDING ? 0 : 1);
        } else {
            boundary = progressed.stream().filter(newPositions::containsKey).mapToInt(newPositions::get).max().orElse(-1) + 1;
        }

        List<CandidateEvaluationRound> remaining = new ArrayList<>(prefix);
        for (int i = 0; i < updated.size(); i++) {
            JobEvaluationPlanRound template = updated.get(i);
            String lineage = template.getLineageId();
            if (progressed.contains(lineage)) continue;
            CandidateEvaluationRound round = pendingByLineage.remove(lineage);
            if (i < boundary) {
                if (round != null) {
                    removed.add(round);
                    ignored = true;
                }
                if (changed(oldByLineage.get(lineage), template)) ignored = true;
                continue;
            }
            if (round == null && !oldByLineage.containsKey(lineage)) {
                round = new CandidateEvaluationRound();
                round.setJobApplicationId(application.getId());
                round.setPlanLineageId(lineage);
            }
            if (round != null) {
                copyTemplate(round, template);
                remaining.add(round);
            }
        }
        if (!pendingByLineage.isEmpty()) throw new InternalServerException("Unmatched candidate evaluation rounds");
        remaining.addAll(pendingCustom);
        for (int i = 0; i < remaining.size(); i++) remaining.get(i).setOrder(i);
        return new Result(List.copyOf(remaining), List.copyOf(removed), ignored);
    }

    private static void copyTemplate(CandidateEvaluationRound round, JobEvaluationPlanRound template) {
        round.setPlanRoundId(template.getId());
        round.setName(template.getName());
        round.setDescription(template.getDescription());
        round.setRoundType(template.getRoundType());
        round.setInterviewers(template.getInterviewers() == null ? null : new ArrayList<>(template.getInterviewers()));
    }

    private static List<JobEvaluationPlanRound> orderedPlan(List<JobEvaluationPlanRound> plan, Long jobId) {
        if (plan == null) throw new InternalServerException("Missing evaluation plan");
        List<JobEvaluationPlanRound> ordered = new ArrayList<>(plan);
        Set<Integer> orders = new HashSet<>();
        Set<String> lineages = new HashSet<>();
        for (JobEvaluationPlanRound round : ordered) {
            if (round == null || round.getId() == null || !Objects.equals(round.getJobPositionId(), jobId)
                    || round.getOrder() == null || round.getOrder() < 0 || !orders.add(round.getOrder())
                    || round.getLineageId() == null || !lineages.add(round.getLineageId())
                    || round.getRoundType() == null || round.getName() == null || round.getName().isBlank()) {
                throw new InternalServerException("Invalid evaluation plan");
            }
        }
        ordered.sort(Comparator.comparing(JobEvaluationPlanRound::getOrder));
        return ordered;
    }

    private static Map<String, JobEvaluationPlanRound> byLineage(List<JobEvaluationPlanRound> plan) {
        Map<String, JobEvaluationPlanRound> result = new HashMap<>();
        for (JobEvaluationPlanRound round : plan) result.put(round.getLineageId(), round);
        return result;
    }

    private static boolean planChanged(List<JobEvaluationPlanRound> before, List<JobEvaluationPlanRound> after) {
        if (before.size() != after.size()) return true;
        for (int i = 0; i < before.size(); i++) {
            if (changed(before.get(i), after.get(i))) return true;
        }
        return false;
    }

    private static boolean changed(JobEvaluationPlanRound before, JobEvaluationPlanRound after) {
        if (before == null || after == null) return before != after;
        return !Objects.equals(before.getLineageId(), after.getLineageId())
                || !Objects.equals(before.getOrder(), after.getOrder())
                || !Objects.equals(before.getName(), after.getName())
                || !Objects.equals(before.getDescription(), after.getDescription())
                || before.getRoundType() != after.getRoundType()
                || !Objects.equals(before.getInterviewers(), after.getInterviewers());
    }
}
