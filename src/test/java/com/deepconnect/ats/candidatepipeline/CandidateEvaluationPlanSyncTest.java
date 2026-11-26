package com.deepconnect.ats.candidatepipeline;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationPlanSync;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationWorkflow;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.jobapplication.model.JobApplication;

class CandidateEvaluationPlanSyncTest {
    private JobApplication application() {
        JobApplication application = new JobApplication();
        application.setId(7L);
        application.setJobPositionId(5L);
        return application;
    }

    private JobEvaluationPlanRound plan(String lineage, long id, int order, String name) {
        JobEvaluationPlanRound round = new JobEvaluationPlanRound();
        round.setId(id);
        round.setJobPositionId(5L);
        round.setLineageId(lineage);
        round.setOrder(order);
        round.setName(name);
        round.setRoundType(EvaluationPlanRoundType.HR);
        round.setInterviewers(List.of("interviewer"));
        return round;
    }

    private List<CandidateEvaluationRound> initialize(JobApplication application, List<JobEvaluationPlanRound> plan) {
        var rounds = new ArrayList<>(CandidateEvaluationWorkflow.initialize(application, plan, List.of()));
        for (var round : rounds) round.setPublicId(round.getPlanLineageId());
        return rounds;
    }

    @Test
    void ignoresInsertedPastRoundAndUpdatesPendingRounds() {
        JobApplication application = application();
        var old = List.of(plan("a", 1, 0, "A"), plan("b", 2, 1, "B"), plan("c", 3, 2, "C"));
        var rounds = initialize(application, old);
        CandidateEvaluationWorkflow.recordResult(application, rounds, "a", CandidateEvaluationResult.PASSED);
        var updated = List.of(plan("a", 11, 0, "Changed A"), plan("x", 12, 1, "New past"),
                plan("b", 13, 2, "Changed B"), plan("c", 14, 3, "Changed C"),
                plan("d", 15, 4, "New future"));

        var result = CandidateEvaluationPlanSync.synchronize(application, rounds, old, updated);

        assertTrue(result.hasIgnoredChanges());
        assertEquals(List.of("a", "b", "c", "d"),
                result.rounds().stream().map(CandidateEvaluationRound::getPlanLineageId).toList());
        assertEquals("A", result.rounds().get(0).getName());
        assertEquals("Changed B", result.rounds().get(1).getName());
        assertEquals(13L, result.rounds().get(1).getPlanRoundId());
        assertEquals(CandidateEvaluationStatus.PENDING, result.rounds().get(3).getStatus());
        assertEquals("b", CandidateEvaluationWorkflow.currentRound(application, result.rounds()).getPublicId());
    }

    @Test
    void removesPendingRoundMovedBeforeCurrentAndWarns() {
        JobApplication application = application();
        var old = List.of(plan("a", 1, 0, "A"), plan("b", 2, 1, "B"), plan("c", 3, 2, "C"));
        var rounds = initialize(application, old);
        CandidateEvaluationWorkflow.recordResult(application, rounds, "a", CandidateEvaluationResult.PASSED);
        var updated = List.of(plan("a", 11, 0, "A"), plan("c", 13, 1, "C"), plan("b", 12, 2, "B"));

        var result = CandidateEvaluationPlanSync.synchronize(application, rounds, old, updated);

        assertTrue(result.hasIgnoredChanges());
        assertEquals(List.of("a", "b"), result.rounds().stream().map(CandidateEvaluationRound::getPlanLineageId).toList());
        assertEquals(List.of("c"), result.removed().stream().map(CandidateEvaluationRound::getPlanLineageId).toList());
    }

    @Test
    void removingCurrentPendingRoundAdvancesToNext() {
        JobApplication application = application();
        var old = List.of(plan("a", 1, 0, "A"), plan("b", 2, 1, "B"), plan("c", 3, 2, "C"));
        var rounds = initialize(application, old);
        CandidateEvaluationWorkflow.recordResult(application, rounds, "a", CandidateEvaluationResult.PASSED);
        var result = CandidateEvaluationPlanSync.synchronize(application, rounds, old,
                List.of(plan("a", 11, 0, "A"), plan("c", 13, 1, "C")));
        assertEquals(List.of("b"), result.removed().stream().map(CandidateEvaluationRound::getPlanLineageId).toList());
        assertEquals("c", CandidateEvaluationWorkflow.currentRound(application, result.rounds()).getPublicId());
    }

    @Test
    void removingLastPendingRoundLeavesEvaluationsComplete() {
        JobApplication application = application();
        var old = List.of(plan("a", 1, 0, "A"), plan("b", 2, 1, "B"));
        var rounds = initialize(application, old);
        CandidateEvaluationWorkflow.recordResult(application, rounds, "a", CandidateEvaluationResult.PASSED);
        var result = CandidateEvaluationPlanSync.synchronize(application, rounds, old, List.of(plan("a", 11, 0, "A")));
        assertFalse(result.hasIgnoredChanges());
        assertNull(CandidateEvaluationWorkflow.currentRound(application, result.rounds()));
        CandidateEvaluationWorkflow.decide(application, result.rounds(), CandidateEvaluationDecision.PASSED);
        assertEquals(List.of("b"), result.removed().stream().map(CandidateEvaluationRound::getPlanLineageId).toList());
    }

    @Test
    void completedEvaluationsIgnoreLaterAdditionsAndPreserveDecision() {
        JobApplication application = application();
        var old = List.of(plan("a", 1, 0, "A"));
        var rounds = initialize(application, old);
        CandidateEvaluationWorkflow.recordResult(application, rounds, "a", CandidateEvaluationResult.PASSED);
        CandidateEvaluationWorkflow.decide(application, rounds, CandidateEvaluationDecision.PASSED);
        var result = CandidateEvaluationPlanSync.synchronize(application, rounds, old,
                List.of(plan("a", 11, 0, "A"), plan("b", 12, 1, "B")));
        assertTrue(result.hasIgnoredChanges());
        assertEquals(1, result.rounds().size());
        assertEquals(CandidateEvaluationDecision.PASSED, application.getEvaluationDecision());
    }

    @Test
    void preservesScheduledRoundAndKeepsCandidateSpecificCurrentBeforeNewPlanRounds() {
        JobApplication application = application();
        var old = List.of(plan("a", 1, 0, "A"));
        var rounds = initialize(application, old);
        rounds.get(0).setStatus(CandidateEvaluationStatus.SCHEDULED);
        var changed = CandidateEvaluationPlanSync.synchronize(application, rounds, old,
                List.of(plan("a", 11, 0, "Changed"), plan("b", 12, 1, "B")));
        assertEquals("A", changed.rounds().get(0).getName());
        assertTrue(changed.hasIgnoredChanges());
        CandidateEvaluationWorkflow.recordResult(application, changed.rounds(), "a", CandidateEvaluationResult.PASSED);
        var onlyA = new ArrayList<>(List.of(changed.rounds().get(0)));
        var extra = CandidateEvaluationWorkflow.addRound(application, onlyA, "Extra", null,
                EvaluationPlanRoundType.CUSTOM, List.of());
        extra.setPublicId("extra");
        onlyA.add(extra);

        var result = CandidateEvaluationPlanSync.synchronize(application, onlyA, old,
                List.of(plan("a", 11, 0, "A"), plan("b", 12, 1, "B")));
        assertEquals("extra", CandidateEvaluationWorkflow.currentRound(application, result.rounds()).getPublicId());
        assertEquals("b", result.rounds().get(2).getPlanLineageId());
    }
}
