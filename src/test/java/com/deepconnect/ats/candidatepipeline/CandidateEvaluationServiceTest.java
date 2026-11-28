package com.deepconnect.ats.candidatepipeline;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationFeedback;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.candidatepipeline.repo.CandidateEvaluationFeedbackRepository;
import com.deepconnect.ats.candidatepipeline.repo.CandidateEvaluationRoundRepository;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationService;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.rbac.enums.PermissionType;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

class CandidateEvaluationServiceTest {
    private final JobPositionRepository positions = mock(JobPositionRepository.class);
    private final JobRecruitmentPipelineRoundRepository stages = mock(JobRecruitmentPipelineRoundRepository.class);
    private final JobApplicationRepository applications = mock(JobApplicationRepository.class);
    private final CandidateEvaluationRoundRepository rounds = mock(CandidateEvaluationRoundRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final CandidateEvaluationFeedbackRepository feedbacks = mock(CandidateEvaluationFeedbackRepository.class);
    private final CandidateEvaluationService service = new CandidateEvaluationService(positions, stages, applications,
            rounds, new JobPositionAccessService(), users, feedbacks);
    private final JobPosition job = new JobPosition();
    private final JobApplication application = new JobApplication();
    private final CandidateEvaluationRound round = new CandidateEvaluationRound();
    private final JobRecruitmentPipelineRound stage = new JobRecruitmentPipelineRound();

    @BeforeEach
    void setUp() {
        login(1L, "recruiter", false);
        job.setId(5L);
        job.setTenantId(2L);
        job.setOwnerId(9L);
        job.setRecruiters(List.of("recruiter", "outsider", "first", "second"));
        job.setHiringManagers(List.of("manager"));
        application.setId(7L);
        application.setJobPositionId(5L);
        application.setRecruitmentPipelineStage(10L);
        application.setEvaluationsInitialized(true);
        stage.setId(10L);
        stage.setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        round.setJobApplicationId(7L);
        round.setPublicId("round");
        round.setOrder(0);
        when(positions.findForUpdate("job", 2L)).thenReturn(Optional.of(job));
        when(positions.findByPublicIdAndTenantIdAndDeactivatedFalse("job", 2L)).thenReturn(Optional.of(job));
        when(applications.findByPublicIdAndJobPositionId("candidate", 5L)).thenReturn(Optional.of(application));
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(stage));
        when(rounds.findByJobApplicationIdOrderByOrderAsc(7L)).thenReturn(List.of(round));
    }

    private void login(long userId, String publicId, boolean admin) {
        var authorities = admin ? List.of(new SimpleGrantedAuthority(PermissionType.JOB_POSITION_ADMIN.getCode())) : List.<SimpleGrantedAuthority>of();
        var user = new LoggedInUserDetails(2L, userId, publicId, "user@example.com", "user name", false, false, authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, authorities));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recruiterCanScheduleButCannotDecide() {
        service.schedule("job", "candidate", "round", Instant.parse("2026-11-25T10:00:00Z"), 45);
        assertEquals(CandidateEvaluationStatus.SCHEDULED, round.getStatus());
        verify(rounds).save(round);
        round.setStatus(CandidateEvaluationStatus.COMPLETED);
        round.setResult(CandidateEvaluationResult.STRONG_YES);
        assertThrows(InternalServerException.class,
                () -> service.decide("job", "candidate", CandidateEvaluationDecision.PASSED));
        assertNull(application.getEvaluationDecision());
    }

    @Test
    void hiringManagerOwnerAndAdminCanDecideOnlyAfterCompletion() {
        login(2L, "manager", false);
        assertThrows(CustomException.class, () -> service.decide("job", "candidate", CandidateEvaluationDecision.PASSED));
        round.setStatus(CandidateEvaluationStatus.COMPLETED);
        round.setResult(CandidateEvaluationResult.STRONG_NO);
        service.decide("job", "candidate", CandidateEvaluationDecision.PASSED);
        assertEquals(CandidateEvaluationDecision.PASSED, application.getEvaluationDecision());
        application.setEvaluationDecision(null);
        login(9L, "owner", false);
        service.decide("job", "candidate", CandidateEvaluationDecision.FAILED);
        assertEquals(CandidateEvaluationDecision.FAILED, application.getEvaluationDecision());
        application.setEvaluationDecision(null);
        login(3L, "admin", true);
        service.decide("job", "candidate", CandidateEvaluationDecision.PASSED);
        assertEquals(CandidateEvaluationDecision.PASSED, application.getEvaluationDecision());
    }

    @Test
    void roundCompletesOnlyAfterEveryInterviewerSubmitsFeedback() {
        round.setStatus(CandidateEvaluationStatus.AWAITING_FEEDBACK);
        round.setInterviewers(List.of("first", "second"));
        List<CandidateEvaluationFeedback> stored = new java.util.ArrayList<>();
        when(feedbacks.findByCandidateEvaluationRoundIdAndInterviewerId(any(), any())).thenAnswer(call ->
                stored.stream().filter(item -> item.getInterviewerId().equals(call.getArgument(1))).findFirst());
        when(feedbacks.findByCandidateEvaluationRoundId(any())).thenReturn(stored);
        when(feedbacks.save(any())).thenAnswer(call -> {
            CandidateEvaluationFeedback saved = call.getArgument(0);
            if (stored.stream().noneMatch(item -> item == saved)) stored.add(saved);
            return saved;
        });

        login(1L, "outsider", false);
        var request = new com.deepconnect.ats.candidatepipeline.dto.CandidateEvaluationDto.FeedbackRequest(
                CandidateEvaluationResult.STRONG_YES, " good ", null, null);
        assertThrows(CustomException.class, () -> service.saveFeedback("job", "candidate", "round", request, true));

        login(1L, "first", false);
        service.saveFeedback("job", "candidate", "round", request, false);
        assertFalse(stored.get(0).isSubmitted());
        assertEquals("good", stored.get(0).getOverallFeedback());
        service.saveFeedback("job", "candidate", "round", request, true);
        assertEquals(CandidateEvaluationStatus.AWAITING_FEEDBACK, round.getStatus());
        assertThrows(CustomException.class, () -> service.saveFeedback("job", "candidate", "round", request, true));

        login(2L, "second", false);
        service.saveFeedback("job", "candidate", "round", new com.deepconnect.ats.candidatepipeline.dto.CandidateEvaluationDto
                .FeedbackRequest(CandidateEvaluationResult.NO, null, null, null), true);
        assertEquals(CandidateEvaluationStatus.CONFLICTING_FEEDBACK, round.getStatus());
        assertNull(round.getResult());
        verify(rounds).save(round);
    }

    @Test
    void rejectsSubmitWithoutResultAndPendingRounds() {
        round.setInterviewers(List.of("first"));
        login(1L, "first", false);
        var request = new com.deepconnect.ats.candidatepipeline.dto.CandidateEvaluationDto.FeedbackRequest(
                CandidateEvaluationResult.YES, null, null, null);
        assertThrows(CustomException.class, () -> service.saveFeedback("job", "candidate", "round", request, true));
        round.setStatus(CandidateEvaluationStatus.SCHEDULED);
        assertThrows(CustomException.class, () -> service.saveFeedback("job", "candidate", "round", request, true));
        round.setStatus(CandidateEvaluationStatus.AWAITING_FEEDBACK);
        assertThrows(CustomException.class, () -> service.saveFeedback("job", "candidate", "round",
                new com.deepconnect.ats.candidatepipeline.dto.CandidateEvaluationDto.FeedbackRequest(null, null, null, null), true));
        verify(feedbacks, never()).save(any());
    }

    @Test
    void submittedFeedbackIsReadableByJobUsersButDraftsArePrivate() {
        round.setId(17L);
        round.setInterviewers(List.of("first", "second"));
        round.setStatus(CandidateEvaluationStatus.COMPLETED);
        round.setResult(CandidateEvaluationResult.YES);
        when(rounds.findByPublicIdAndJobApplicationId("round", 7L)).thenReturn(Optional.of(round));
        CandidateEvaluationFeedback submitted = new CandidateEvaluationFeedback();
        submitted.setInterviewerId("first");
        submitted.setSubmitted(true);
        submitted.setOverallFeedback("Feedback");
        CandidateEvaluationFeedback draft = new CandidateEvaluationFeedback();
        draft.setInterviewerId("second");
        when(feedbacks.findByCandidateEvaluationRoundIdAndInterviewerId(17L, "first")).thenReturn(Optional.of(submitted));
        when(feedbacks.findByCandidateEvaluationRoundIdAndInterviewerId(17L, "second")).thenReturn(Optional.of(draft));

        assertEquals("Feedback", service.getSubmittedFeedback("job", "candidate", "round", "first").overallFeedback());
        assertThrows(CustomException.class, () -> service.getSubmittedFeedback("job", "candidate", "round", "second"));
        login(3L, "stranger", false);
        assertThrows(InternalServerException.class,
                () -> service.getSubmittedFeedback("job", "candidate", "round", "first"));
    }

    @Test
    void evaluationResponseIdentifiesSubmittedInterviewersAndOwnFeedbackAction() {
        round.setId(17L);
        round.setStatus(CandidateEvaluationStatus.AWAITING_FEEDBACK);
        round.setInterviewers(List.of("recruiter", "first"));
        CandidateEvaluationFeedback submitted = new CandidateEvaluationFeedback();
        submitted.setCandidateEvaluationRoundId(17L);
        submitted.setInterviewerId("first");
        submitted.setSubmitted(true);
        submitted.setResult(CandidateEvaluationResult.STRONG_YES);
        submitted.setSubmittedAt(Instant.parse("2026-11-25T10:00:00Z"));
        when(feedbacks.findByCandidateEvaluationRoundIdInAndSubmittedTrue(List.of(17L))).thenReturn(List.of(submitted));

        var response = service.get("job", "candidate");
        assertEquals("recruiter", response.currentUserId());
        assertTrue(response.rounds().get(0).canEnterFeedback());
        assertEquals(List.of("first"), response.rounds().get(0).submittedInterviewers());
        var feedbackSummary = response.rounds().get(0).submittedFeedback().get(0);
        assertEquals("first", feedbackSummary.interviewerId());
        assertEquals(CandidateEvaluationResult.STRONG_YES, feedbackSummary.result());
        assertEquals(Instant.parse("2026-11-25T10:00:00Z"), feedbackSummary.submittedAt());
        var json = new ObjectMapper().findAndRegisterModules()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).valueToTree(response);
        var summaryJson = json.path("rounds").get(0).path("submitted_feedback").get(0);
        assertEquals("first", summaryJson.path("interviewer_id").asText());
        assertEquals(0, summaryJson.path("result").asInt());
        assertTrue(summaryJson.hasNonNull("submitted_at"));
        round.setStatus(CandidateEvaluationStatus.SCHEDULED);
        assertFalse(service.get("job", "candidate").rounds().get(0).canEnterFeedback());
    }

    @Test
    void readingEvaluationsAdvancesAndPersistsAnOverdueCurrentRound() {
        round.setId(17L);
        round.setStatus(CandidateEvaluationStatus.SCHEDULED);
        round.setScheduledAt(Instant.now().minusSeconds(3600));
        round.setDurationMinutes(60);
        round.setInterviewers(List.of("recruiter"));

        var response = service.get("job", "candidate");
        assertEquals(CandidateEvaluationStatus.AWAITING_FEEDBACK, response.rounds().get(0).status());
        assertTrue(response.rounds().get(0).canEnterFeedback());
        assertNotNull(round.getStatusChangedAt());
        verify(rounds).save(round);
        verify(positions).findForUpdate("job", 2L);
    }

    @Test
    void accessingOwnFeedbackAdvancesAnOverdueScheduledRound() {
        round.setId(17L);
        round.setStatus(CandidateEvaluationStatus.SCHEDULED);
        round.setScheduledAt(Instant.now().minusSeconds(1200));
        round.setDurationMinutes(10);
        round.setInterviewers(List.of("recruiter"));
        assertFalse(service.getFeedback("job", "candidate", "round").submitted());
        assertEquals(CandidateEvaluationStatus.AWAITING_FEEDBACK, round.getStatus());
        verify(rounds).save(round);
    }

    @Test
    void submittingFeedbackDirectlyAdvancesAnOverdueScheduledRound() {
        round.setId(17L);
        round.setStatus(CandidateEvaluationStatus.SCHEDULED);
        round.setScheduledAt(Instant.now().minusSeconds(1200));
        round.setDurationMinutes(10);
        round.setInterviewers(List.of("recruiter"));
        CandidateEvaluationFeedback submitted = new CandidateEvaluationFeedback();
        submitted.setInterviewerId("recruiter");
        submitted.setSubmitted(true);
        submitted.setResult(CandidateEvaluationResult.YES);
        when(feedbacks.findByCandidateEvaluationRoundId(17L)).thenReturn(List.of(submitted));
        service.saveFeedback("job", "candidate", "round",
                new com.deepconnect.ats.candidatepipeline.dto.CandidateEvaluationDto.FeedbackRequest(
                        CandidateEvaluationResult.YES, null, null, null), true);
        assertEquals(CandidateEvaluationStatus.COMPLETED, round.getStatus());
        assertEquals(CandidateEvaluationResult.YES, round.getResult());
        verify(rounds, times(2)).save(round);
    }

    @Test
    void readingClosedOrNonEvaluatingApplicationDoesNotAdvanceRound() {
        round.setStatus(CandidateEvaluationStatus.SCHEDULED);
        round.setScheduledAt(Instant.now().minusSeconds(3600));
        round.setDurationMinutes(30);
        application.setClosed(true);
        service.get("job", "candidate");
        application.setClosed(false);
        stage.setRoundType(RecruitmentPipelineRoundType.SCREENING);
        service.get("job", "candidate");
        assertEquals(CandidateEvaluationStatus.SCHEDULED, round.getStatus());
        verify(rounds, never()).save(any());
    }

    @Test
    void rejectsInterviewerFromAnotherTenant() {
        User other = new User();
        other.setPublicId("other");
        other.setTenantId(3L);
        when(users.findByPublicIdInAndDeactivatedFalseAndLockedFalse(Set.of("other"))).thenReturn(Set.of(other));
        assertThrows(CustomException.class,
                () -> service.changeInterviewers("job", "candidate", "round", List.of("other")));
        verify(rounds, never()).save(any());
    }

    @Test
    void rejectsMutationsOutsideEvaluationStage() {
        stage.setRoundType(RecruitmentPipelineRoundType.SCREENING);
        assertThrows(CustomException.class,
                () -> service.recordResult("job", "candidate", "round", CandidateEvaluationResult.STRONG_YES));
        verify(rounds, never()).save(any());
    }

    @Test
    void onlyOwnerOrHiringManagerCanResolveConflictingFeedback() {
        round.setStatus(CandidateEvaluationStatus.CONFLICTING_FEEDBACK);
        assertFalse(service.get("job", "candidate").canResolveConflict());
        assertThrows(InternalServerException.class,
                () -> service.recordResult("job", "candidate", "round", CandidateEvaluationResult.YES));
        login(3L, "admin", true);
        assertTrue(service.get("job", "candidate").canDecide());
        assertFalse(service.get("job", "candidate").canResolveConflict());
        assertThrows(InternalServerException.class,
                () -> service.recordResult("job", "candidate", "round", CandidateEvaluationResult.YES));
        verify(rounds, never()).save(any());
        login(2L, "manager", false);
        assertTrue(service.get("job", "candidate").canResolveConflict());
        service.recordResult("job", "candidate", "round", CandidateEvaluationResult.YES);
        assertEquals(CandidateEvaluationStatus.COMPLETED, round.getStatus());
        assertEquals(CandidateEvaluationResult.YES, round.getResult());
        round.setResult(null);
        round.setStatus(CandidateEvaluationStatus.CONFLICTING_FEEDBACK);
        login(9L, "owner", false);
        assertTrue(service.get("job", "candidate").canResolveConflict());
        service.recordResult("job", "candidate", "round", CandidateEvaluationResult.NO);
        assertEquals(CandidateEvaluationResult.NO, round.getResult());
    }

    @Test
    void readExposesOnlyPublicRoundDataAndDecisionPermission() {
        round.setName("Interview");
        round.setScheduledAt(Instant.parse("2026-11-25T10:00:00Z"));
        var response = service.get("job", "candidate");
        var json = new ObjectMapper().findAndRegisterModules()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).valueToTree(response);
        assertTrue(json.path("initialized").asBoolean());
        assertTrue(json.path("can_manage").asBoolean());
        assertFalse(json.path("can_decide").asBoolean());
        assertEquals("round", json.path("rounds").get(0).path("id").asText());
        assertEquals(0, json.path("rounds").get(0).path("status").asInt());
        assertFalse(json.path("rounds").get(0).has("job_application_id"));
        assertFalse(json.path("rounds").get(0).has("plan_round_id"));
        login(2L, "manager", false);
        assertTrue(service.get("job", "candidate").canDecide());
        stage.setRoundType(RecruitmentPipelineRoundType.SCREENING);
        assertFalse(service.get("job", "candidate").canManage());
        stage.setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        application.setClosed(true);
        assertFalse(service.get("job", "candidate").canManage());
    }

    @Test
    void scheduleUpdatesTimeAndDurationWithoutChangingInterviewers() {
        round.setInterviewers(List.of("interviewer"));
        var time = Instant.parse("2026-11-25T10:00:00Z");
        service.schedule("job", "candidate", "round", time, 45);
        assertEquals(time, round.getScheduledAt());
        assertEquals(45, round.getDurationMinutes());
        assertNotNull(round.getStatusChangedAt());
        assertEquals(List.of("interviewer"), round.getInterviewers());
        clearInvocations(rounds);
        assertThrows(CustomException.class, () -> service.schedule("job", "candidate", "round",
                time.plusSeconds(60), 0));
        assertEquals(time, round.getScheduledAt());
        assertEquals(List.of("interviewer"), round.getInterviewers());
        verify(rounds, never()).save(any());
    }

    @Test
    void deniesEvaluationReadWithoutJobAccessOrCandidateMembership() {
        login(3L, "stranger", false);
        assertThrows(InternalServerException.class, () -> service.get("job", "candidate"));
        verify(rounds, never()).findByJobApplicationIdOrderByOrderAsc(anyLong());
        verify(users, never()).findByTenantIdAndDeactivatedFalseAndLockedFalse(anyLong());
        login(1L, "recruiter", false);
        assertThrows(CustomException.class, () -> service.get("job", "other-candidate"));
        verify(rounds, never()).findByJobApplicationIdOrderByOrderAsc(anyLong());
        verify(users, never()).findByTenantIdAndDeactivatedFalseAndLockedFalse(anyLong());
    }
}
