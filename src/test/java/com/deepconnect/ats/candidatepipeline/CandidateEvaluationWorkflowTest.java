package com.deepconnect.ats.candidatepipeline;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationFeedback;
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
                "second", CandidateEvaluationResult.STRONG_YES));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.decide(application, rounds,
                CandidateEvaluationDecision.PASSED));

        CandidateEvaluationWorkflow.recordResult(application, rounds, "first", CandidateEvaluationResult.STRONG_NO);
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
        assertNotNull(rounds.get(0).getStatusChangedAt());
        CandidateEvaluationWorkflow.changeStatus(application, rounds, "first", CandidateEvaluationStatus.AWAITING_FEEDBACK);
        Instant changedAt = rounds.get(0).getStatusChangedAt();
        assertNotNull(changedAt);
        CandidateEvaluationWorkflow.changeStatus(application, rounds, "first", CandidateEvaluationStatus.AWAITING_FEEDBACK);
        assertEquals(changedAt, rounds.get(0).getStatusChangedAt());
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeInterviewers(application, rounds,
                "first", List.of("new-user")));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeStatus(application, rounds,
                "second", CandidateEvaluationStatus.AWAITING_FEEDBACK));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeStatus(application, rounds,
                "first", CandidateEvaluationStatus.COMPLETED));
        assertNull(rounds.get(0).getResult());
    }

    @Test
    void advancesOnlyCurrentScheduledRoundAfterTheApplicableThreshold() {
        JobApplication application = application();
        List<CandidateEvaluationRound> rounds = CandidateEvaluationWorkflow.initialize(application,
                List.of(template(10, 0), template(11, 1)), List.of());
        CandidateEvaluationRound current = rounds.get(0);
        CandidateEvaluationRound later = rounds.get(1);
        Instant scheduledAt = Instant.parse("2026-11-25T10:00:00Z");
        current.setStatus(CandidateEvaluationStatus.SCHEDULED);
        current.setScheduledAt(scheduledAt);
        current.setDurationMinutes(60);
        later.setStatus(CandidateEvaluationStatus.SCHEDULED);
        later.setScheduledAt(scheduledAt);
        later.setDurationMinutes(10);

        assertNull(CandidateEvaluationWorkflow.advanceScheduledRound(application, rounds, scheduledAt.plusSeconds(1800)));
        assertEquals(CandidateEvaluationStatus.SCHEDULED, later.getStatus());
        Instant afterThreshold = scheduledAt.plusSeconds(1801);
        assertSame(current, CandidateEvaluationWorkflow.advanceScheduledRound(application, rounds, afterThreshold));
        assertEquals(CandidateEvaluationStatus.AWAITING_FEEDBACK, current.getStatus());
        assertEquals(afterThreshold, current.getStatusChangedAt());
        assertEquals(CandidateEvaluationStatus.SCHEDULED, later.getStatus());
        assertNull(CandidateEvaluationWorkflow.advanceScheduledRound(application, rounds, afterThreshold.plusSeconds(1)));
        assertEquals(afterThreshold, current.getStatusChangedAt());

        current.setStatus(CandidateEvaluationStatus.COMPLETED);
        current.setResult(CandidateEvaluationResult.YES);
        assertSame(later, CandidateEvaluationWorkflow.advanceScheduledRound(application, rounds, scheduledAt.plusSeconds(601)));
        assertEquals(CandidateEvaluationStatus.AWAITING_FEEDBACK, later.getStatus());
    }

    @Test
    void doesNotAdvanceAtShortOrThirtyMinuteBoundaryOrWithMissingDuration() {
        JobApplication application = application();
        List<CandidateEvaluationRound> rounds = CandidateEvaluationWorkflow.initialize(application, List.of(template(10, 0)), List.of());
        CandidateEvaluationRound round = rounds.get(0);
        Instant scheduledAt = Instant.parse("2026-11-25T10:00:00Z");
        round.setStatus(CandidateEvaluationStatus.SCHEDULED);
        round.setScheduledAt(scheduledAt);
        round.setDurationMinutes(10);
        assertNull(CandidateEvaluationWorkflow.advanceScheduledRound(application, rounds, scheduledAt.plusSeconds(600)));
        assertSame(round, CandidateEvaluationWorkflow.advanceScheduledRound(application, rounds, scheduledAt.plusSeconds(601)));
        round.setStatus(CandidateEvaluationStatus.SCHEDULED);
        round.setDurationMinutes(30);
        assertNull(CandidateEvaluationWorkflow.advanceScheduledRound(application, rounds, scheduledAt.plusSeconds(1800)));
        assertSame(round, CandidateEvaluationWorkflow.advanceScheduledRound(application, rounds, scheduledAt.plusSeconds(1801)));
        round.setStatus(CandidateEvaluationStatus.SCHEDULED);
        round.setDurationMinutes(null);
        assertNull(CandidateEvaluationWorkflow.advanceScheduledRound(application, rounds, scheduledAt.plusSeconds(3600)));
        assertEquals(CandidateEvaluationStatus.SCHEDULED, round.getStatus());
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
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeInterviewers(application, rounds,
                "extra", List.of("new-user")));
        CandidateEvaluationWorkflow.recordResult(application, rounds, "first", CandidateEvaluationResult.STRONG_YES);
        assertEquals("extra", CandidateEvaluationWorkflow.currentRound(application, rounds).getPublicId());
        CandidateEvaluationWorkflow.changeInterviewers(application, rounds, "extra", List.of("new-user"));
        assertEquals(List.of("new-user"), extra.getInterviewers());
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeInterviewers(application, rounds,
                "first", List.of("new-user")));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeInterviewers(application, rounds,
                "extra", List.of("same", "same")));
    }

    @Test
    void aggregatesSameSideRatingsWithTiesFavoringOrdinaryRatings() {
        assertAggregate(CandidateEvaluationResult.STRONG_YES, CandidateEvaluationResult.STRONG_YES);
        assertAggregate(CandidateEvaluationResult.NO, CandidateEvaluationResult.NO);
        assertAggregate(CandidateEvaluationResult.YES, CandidateEvaluationResult.STRONG_YES, CandidateEvaluationResult.YES);
        assertAggregate(CandidateEvaluationResult.NO, CandidateEvaluationResult.STRONG_NO, CandidateEvaluationResult.NO);
        assertAggregate(CandidateEvaluationResult.STRONG_YES, CandidateEvaluationResult.STRONG_YES,
                CandidateEvaluationResult.STRONG_YES, CandidateEvaluationResult.YES);
        assertAggregate(CandidateEvaluationResult.NO, CandidateEvaluationResult.NO, CandidateEvaluationResult.NO,
                CandidateEvaluationResult.NO, CandidateEvaluationResult.STRONG_NO);
        assertAggregate(CandidateEvaluationResult.STRONG_NO, CandidateEvaluationResult.STRONG_NO,
                CandidateEvaluationResult.STRONG_NO, CandidateEvaluationResult.NO);
        assertAggregate(CandidateEvaluationResult.YES, CandidateEvaluationResult.YES, CandidateEvaluationResult.YES,
                CandidateEvaluationResult.STRONG_YES);
    }

    private void assertAggregate(CandidateEvaluationResult expected, CandidateEvaluationResult... results) {
        CandidateEvaluationRound round = new CandidateEvaluationRound();
        round.setStatus(CandidateEvaluationStatus.AWAITING_FEEDBACK);
        List<String> interviewers = new ArrayList<>();
        List<CandidateEvaluationFeedback> feedbacks = new ArrayList<>();
        for (int i = 0; i < results.length; i++) {
            String id = "interviewer-" + i;
            interviewers.add(id);
            CandidateEvaluationFeedback feedback = new CandidateEvaluationFeedback();
            feedback.setInterviewerId(id);
            feedback.setSubmitted(true);
            feedback.setResult(results[i]);
            feedbacks.add(feedback);
        }
        round.setInterviewers(interviewers);
        assertTrue(CandidateEvaluationWorkflow.completeIfAllSubmitted(round, feedbacks));
        assertEquals(CandidateEvaluationStatus.COMPLETED, round.getStatus());
        assertEquals(expected, round.getResult());
    }

    @Test
    void conflictRequiresAllFeedbackAndCannotBeBypassed() {
        JobApplication application = application();
        JobEvaluationPlanRound template = template(10, 0);
        template.setInterviewers(List.of("first", "second", "third"));
        List<CandidateEvaluationRound> rounds = CandidateEvaluationWorkflow.initialize(application, List.of(template), List.of());
        CandidateEvaluationRound round = rounds.get(0);
        round.setPublicId("first");
        round.setStatus(CandidateEvaluationStatus.AWAITING_FEEDBACK);
        List<CandidateEvaluationFeedback> feedbacks = new ArrayList<>();
        for (String id : template.getInterviewers()) {
            CandidateEvaluationFeedback feedback = new CandidateEvaluationFeedback();
            feedback.setInterviewerId(id);
            feedback.setResult(id.equals("third") ? CandidateEvaluationResult.NO : CandidateEvaluationResult.STRONG_YES);
            feedback.setSubmitted(!id.equals("third"));
            feedbacks.add(feedback);
        }
        assertFalse(CandidateEvaluationWorkflow.completeIfAllSubmitted(round, feedbacks));
        assertEquals(CandidateEvaluationStatus.AWAITING_FEEDBACK, round.getStatus());
        feedbacks.get(2).setSubmitted(true);
        assertTrue(CandidateEvaluationWorkflow.completeIfAllSubmitted(round, feedbacks));
        assertEquals(CandidateEvaluationStatus.CONFLICTING_FEEDBACK, round.getStatus());
        assertNull(round.getResult());
        assertNotNull(round.getStatusChangedAt());
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.schedule(application, rounds, "first", Instant.now()));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeStatus(application, rounds, "first", CandidateEvaluationStatus.PENDING));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.changeStatus(application, rounds, "first", CandidateEvaluationStatus.CONFLICTING_FEEDBACK));
        assertThrows(CustomException.class, () -> CandidateEvaluationWorkflow.recordResult(application, rounds, "first", CandidateEvaluationResult.CANCELLED));
        CandidateEvaluationWorkflow.recordResult(application, rounds, "first", CandidateEvaluationResult.NO);
        assertEquals(CandidateEvaluationStatus.COMPLETED, round.getStatus());
        assertEquals(CandidateEvaluationResult.NO, round.getResult());
    }
}
