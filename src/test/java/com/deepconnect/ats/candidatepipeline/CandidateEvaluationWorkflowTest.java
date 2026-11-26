package com.deepconnect.ats.candidatepipeline;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationWorkflow;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.jobapplication.model.JobApplication;

class CandidateEvaluationWorkflowTest {
    private JobApplication application() {
        JobApplication application = new JobApplication();
        application.setId(7L);
        application.setJobPositionId(5L);
        return application;
    }

    private JobEvaluationPlanRound template(long id, int order) {
        JobEvaluationPlanRound round = new JobEvaluationPlanRound();
        round.setId(id);
        round.setJobPositionId(5L);
        round.setOrder(order);
        round.setName("Round " + id);
        round.setRoundType(EvaluationPlanRoundType.HR);
        round.setInterviewers(new ArrayList<>(List.of("interviewer")));
        return round;
    }

    @Test
    void initializesFromArchivedPlanRounds() {
        JobApplication application = application();
        JobEvaluationPlanRound archived = template(10, 0);
        archived.setActive(false);

        List<CandidateEvaluationRound> rounds = CandidateEvaluationWorkflow.initialize(application, List.of(archived), List.of());

        assertTrue(application.isEvaluationsInitialized());
        assertEquals(10L, rounds.get(0).getPlanRoundId());
        assertEquals("Round 10", rounds.get(0).getName());
        assertEquals(CandidateEvaluationStatus.PENDING, rounds.get(0).getStatus());
    }

    @Test
    void rejectsDuplicatePlanRoundIds() {
        assertThrows(InternalServerException.class, () -> CandidateEvaluationWorkflow.initialize(application(),
                List.of(template(10, 0), template(10, 1)), List.of()));
    }

    @Test
    void initializesSnapshotsAndProgressesSequentiallyForEveryResult() {
        JobApplication application = application();
        JobEvaluationPlanRound first = template(10, 0);
        JobEvaluationPlanRound second = template(11, 1);
        List<CandidateEvaluationRound> rounds = CandidateEvaluationWorkflow.initialize(application,
                List.of(second, first), List.of());
        rounds.get(0).setPublicId("first");
        rounds.get(1).setPublicId("second");
        first.getInterviewers().clear();
        assertEquals(List.of("interviewer"), rounds.get(0).getInterviewers());
        assertEquals(10L, rounds.get(0).getPlanRoundId());
        assertEquals(CandidateEvaluationStatus.PENDING, rounds.get(0).getStatus());
        assertEquals("first", CandidateEvaluationWorkflow.currentRound(application, rounds).getPublicId());
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.recordResult(application, rounds,
                "second", CandidateEvaluationResult.PASSED));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.decide(application, rounds,
                CandidateEvaluationDecision.PASSED));

        CandidateEvaluationWorkflow.recordResult(application, rounds, "first", CandidateEvaluationResult.FAILED);
        assertEquals("second", CandidateEvaluationWorkflow.currentRound(application, rounds).getPublicId());
        CandidateEvaluationWorkflow.recordResult(application, rounds, "second", CandidateEvaluationResult.CANCELLED);
        assertNull(CandidateEvaluationWorkflow.currentRound(application, rounds));
        CandidateEvaluationWorkflow.decide(application, rounds, CandidateEvaluationDecision.PASSED);
        assertEquals(CandidateEvaluationDecision.PASSED, application.getEvaluationDecision());
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.decide(application, rounds,
                CandidateEvaluationDecision.FAILED));
    }

    @Test
    void schedulesAndChangesStatusOnlyForCurrentRound() {
        JobApplication application = application();
        List<CandidateEvaluationRound> rounds = CandidateEvaluationWorkflow.initialize(application,
                List.of(template(10, 0), template(11, 1)), List.of());
        rounds.get(0).setPublicId("first");
        rounds.get(1).setPublicId("second");
        Instant time = Instant.parse("2026-11-25T10:00:00Z");
        CandidateEvaluationWorkflow.schedule(application, rounds, "first", time);
        assertEquals(CandidateEvaluationStatus.SCHEDULED, rounds.get(0).getStatus());
        assertEquals(time, rounds.get(0).getScheduledAt());
        CandidateEvaluationWorkflow.changeStatus(application, rounds, "first", CandidateEvaluationStatus.AWAITING_FEEDBACK);
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeStatus(application, rounds,
                "second", CandidateEvaluationStatus.IN_PROGRESS));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeStatus(application, rounds,
                "first", CandidateEvaluationStatus.COMPLETED));
        assertNull(rounds.get(0).getResult());
    }

    @Test
    void permitsDecisionWhenLastPendingRoundWasRemoved() {
        JobApplication application = application();
        CandidateEvaluationWorkflow.initialize(application, List.of(template(10, 0)), List.of());
        CandidateEvaluationWorkflow.decide(application, List.of(), CandidateEvaluationDecision.FAILED);
        assertEquals(CandidateEvaluationDecision.FAILED, application.getEvaluationDecision());
    }

    @Test
    void rejectsAnotherApplicationsRoundAndDuplicateInitialization() {
        JobApplication application = application();
        List<CandidateEvaluationRound> rounds = CandidateEvaluationWorkflow.initialize(application,
                List.of(template(10, 0)), List.of());
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.initialize(application,
                List.of(template(10, 0)), rounds));
        rounds.get(0).setJobApplicationId(99L);
        assertThrows(com.deepconnect.ats.common.exception.InternalServerException.class,
                () -> CandidateEvaluationWorkflow.currentRound(application, rounds));
    }

    @Test
    void addsCandidateSpecificRoundAndProtectsCompletedInterviewers() {
        JobApplication application = application();
        List<CandidateEvaluationRound> rounds = new ArrayList<>(CandidateEvaluationWorkflow.initialize(application,
                List.of(template(10, 0)), List.of()));
        rounds.get(0).setPublicId("first");
        var extra = CandidateEvaluationWorkflow.addRound(application, rounds, "Extra interview", null,
                EvaluationPlanRoundType.CUSTOM, List.of("another-user"));
        extra.setPublicId("extra");
        rounds.add(extra);
        assertNull(extra.getPlanRoundId());
        assertEquals(1, extra.getOrder());
        assertEquals(CandidateEvaluationStatus.PENDING, extra.getStatus());
        CandidateEvaluationWorkflow.changeInterviewers(application, rounds, "extra", List.of("new-user"));
        assertEquals(List.of("new-user"), extra.getInterviewers());
        CandidateEvaluationWorkflow.recordResult(application, rounds, "first", CandidateEvaluationResult.PASSED);
        assertEquals("extra", CandidateEvaluationWorkflow.currentRound(application, rounds).getPublicId());
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeInterviewers(application, rounds,
                "first", List.of("new-user")));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeInterviewers(application, rounds,
                "extra", List.of("same", "same")));
    }
}
