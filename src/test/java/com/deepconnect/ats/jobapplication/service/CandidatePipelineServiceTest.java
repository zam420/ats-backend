package com.deepconnect.ats.jobapplication.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.candidatepipeline.dto.MoveCandidateReq;
import com.deepconnect.ats.candidatepipeline.dto.RejectCandidateReq;
import com.deepconnect.ats.candidatepipeline.service.CandidatePipelineService;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationService;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.candidatepipeline.repo.CandidateEvaluationRoundRepository;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobEvaluationPlanRoundRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.enums.JobApplicationCloseType;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;

class CandidatePipelineServiceTest {
    private final JobPositionRepository positions = mock(JobPositionRepository.class);
    private final JobRecruitmentPipelineRoundRepository stages = mock(JobRecruitmentPipelineRoundRepository.class);
    private final JobApplicationRepository applications = mock(JobApplicationRepository.class);
    private final JobPositionAccessService access = mock(JobPositionAccessService.class);
    private final CandidateEvaluationService evaluations = mock(CandidateEvaluationService.class);
    private final CandidateEvaluationRoundRepository evaluationRounds = mock(CandidateEvaluationRoundRepository.class);
    private final JobEvaluationPlanRoundRepository evaluationPlans = mock(JobEvaluationPlanRoundRepository.class);
    private final CandidatePipelineService service = new CandidatePipelineService(positions, stages, applications, access,         evaluations, evaluationRounds, evaluationPlans);
    private final JobApplication application = new JobApplication();

    @BeforeEach
    void setUp() {
        var user = new LoggedInUserDetails(2L, 1L, "user", "user@example.com", "user name", false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        JobPosition job = new JobPosition();
        job.setId(5L);
        when(positions.findForUpdate("job", 2L)).thenReturn(Optional.of(job));
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(stage(10L, "a"), stage(11L, "b"), stage(12L, "c")));
        application.setRecruitmentPipelineStage(10L);
        when(applications.findByPublicIdAndJobPositionId("candidate", 5L)).thenReturn(Optional.of(application));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private JobRecruitmentPipelineRound stage(Long id, String publicId) {
        var stage = new JobRecruitmentPipelineRound();
        stage.setId(id);
        stage.setPublicId(publicId);
        stage.setOrder(id.intValue() - 10);
        stage.setRoundType(RecruitmentPipelineRoundType.APPLIED);
        stage.setName("Stage " + publicId);
        return stage;
    }

    @Test
    void movesOnlyToImmediateNextStage() {
        service.move("job", "candidate", new MoveCandidateReq("a", "b"));
        assertEquals(11L, application.getRecruitmentPipelineStage());
        verify(applications).save(application);
        verify(access).assertReadAccess(any(), any());
    }

    @Test
    void rejectsSkippingBackwardAndStaleMoves() {
        assertThrows(CustomException.class, () -> service.move("job", "candidate", new MoveCandidateReq("a", "c")));
        assertThrows(CustomException.class, () -> service.move("job", "candidate", new MoveCandidateReq("b", "a")));
        assertThrows(CustomException.class, () -> service.move("job", "candidate", new MoveCandidateReq("b", "c")));
        assertEquals(10L, application.getRecruitmentPipelineStage());
        verify(applications, never()).save(any());
    }

    @Test
    void initializesEvaluationsWhenMovingIntoConfiguredEvaluationStage() {
        var a = stage(10L, "a");
        var evaluating = stage(11L, "evaluating");
        evaluating.setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(a, evaluating));
        application.setEvaluationPlanVersion(1);
        var oldRound = new com.deepconnect.ats.job.model.JobEvaluationPlanRound();
        oldRound.setActive(false);
        var plan = List.of(oldRound);
        var currentRound = new com.deepconnect.ats.job.model.JobEvaluationPlanRound();
        currentRound.setPlanVersion(2);
        positions.findForUpdate("job", 2L).orElseThrow().setEvaluationPlanRounds(List.of(currentRound));
        when(evaluationPlans.findByJobPositionIdAndPlanVersionOrderByOrderAsc(5L, 1)).thenReturn(plan);
        service.move("job", "candidate", new MoveCandidateReq("a", "evaluating"));
        verify(evaluations).initializeLocked(application, plan);
        assertEquals(11L, application.getRecruitmentPipelineStage());
    }

    @Test
    void rejectsMovingIntoEvaluationsWhenApplicationVersionIsUnknown() {
        var a = stage(10L, "a");
        var evaluating = stage(11L, "evaluating");
        evaluating.setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(a, evaluating));

        assertThrows(InternalServerException.class,
                () -> service.move("job", "candidate", new MoveCandidateReq("a", "evaluating")));
        verify(evaluations, never()).initializeLocked(any(), any());
        assertEquals(10L, application.getRecruitmentPipelineStage());
    }

    @Test
    void leavingEvaluationsRequiresCompletedRoundsAndOverallPass() {
        var evaluating = stage(10L, "a");
        evaluating.setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        var next = stage(11L, "b");
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(evaluating, next));
        application.setId(7L);
        assertThrows(CustomException.class, () -> service.move("job", "candidate", new MoveCandidateReq("a", "b")));
        application.setEvaluationsInitialized(true);
        application.setEvaluationDecision(CandidateEvaluationDecision.FAILED);
        assertThrows(CustomException.class, () -> service.move("job", "candidate", new MoveCandidateReq("a", "b")));
        application.setEvaluationDecision(CandidateEvaluationDecision.PASSED);
        var round = new CandidateEvaluationRound();
        round.setJobApplicationId(7L);
        round.setOrder(0);
        when(evaluationRounds.findByJobApplicationIdOrderByOrderAsc(7L)).thenReturn(List.of(round));
        assertThrows(CustomException.class, () -> service.move("job", "candidate", new MoveCandidateReq("a", "b")));
        assertEquals(10L, application.getRecruitmentPipelineStage());
        round.setStatus(CandidateEvaluationStatus.COMPLETED);
        round.setResult(CandidateEvaluationResult.PASSED);
        service.move("job", "candidate", new MoveCandidateReq("a", "b"));
        assertEquals(11L, application.getRecruitmentPipelineStage());
    }

    @Test
    void rejectionFromEvaluationsRequiresOverallFail() {
        var evaluating = stage(10L, "a");
        evaluating.setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(evaluating));
        application.setId(7L);
        application.setEvaluationsInitialized(true);
        application.setEvaluationDecision(CandidateEvaluationDecision.PASSED);
        assertThrows(CustomException.class, () -> service.reject("job", "candidate", new RejectCandidateReq("a", null, false)));
        assertFalse(application.isClosed());
        application.setEvaluationDecision(CandidateEvaluationDecision.FAILED);
        when(evaluationRounds.findByJobApplicationIdOrderByOrderAsc(7L)).thenReturn(List.of());
        service.reject("job", "candidate", new RejectCandidateReq("a", null, false));
        assertTrue(application.isClosed());
    }

    @Test
    void showsEvaluationProgressAndResultCountsOnTheBoard() {
        JobPosition job = new JobPosition();
        job.setId(5L);
        job.setPublicId("job");
        when(positions.findByPublicIdAndTenantIdAndDeactivatedFalse("job", 2L)).thenReturn(Optional.of(job));
        var stage = stage(10L, "a");
        stage.setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(stage));
        application.setId(7L);
        application.setEvaluationsInitialized(true);
        when(applications.findByJobPositionIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(application));
        var first = new CandidateEvaluationRound();
        first.setJobApplicationId(7L);
        first.setOrder(0);
        first.setName("First");
        first.setStatus(CandidateEvaluationStatus.COMPLETED);
        first.setResult(CandidateEvaluationResult.PASSED);
        var last = new CandidateEvaluationRound();
        last.setJobApplicationId(7L);
        last.setOrder(1);
        last.setName("Final");
        last.setStatus(CandidateEvaluationStatus.SCHEDULED);
        when(evaluationRounds.findByJobApplicationIdInOrderByOrderAsc(List.of(7L))).thenReturn(List.of(first, last));
        var summary = service.board("job").getStages().get(0).getCandidates().get(0).getEvaluation();
        assertEquals("Final", summary.currentRoundName());
        assertEquals(CandidateEvaluationStatus.SCHEDULED, summary.currentStatus());
        assertEquals(1, summary.completedRounds());
        assertEquals(2, summary.totalRounds());
        assertEquals(1, summary.passed());
        var json = new ObjectMapper().findAndRegisterModules()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .valueToTree(service.board("job")).path("stages").get(0).path("candidates").get(0).path("evaluation");
        assertEquals("Final", json.path("current_round_name").asText());
        assertEquals(1, json.path("current_status").asInt());
        assertEquals(1, json.path("completed_rounds").asInt());
        assertEquals(2, json.path("total_rounds").asInt());
        last.setStatus(CandidateEvaluationStatus.COMPLETED);
        last.setResult(CandidateEvaluationResult.FAILED);
        summary = service.board("job").getStages().get(0).getCandidates().get(0).getEvaluation();
        assertEquals("Final", summary.currentRoundName());
        assertEquals(CandidateEvaluationStatus.COMPLETED, summary.currentStatus());
        assertEquals(1, summary.failed());
    }

    @Test
    void recordsRejectionAndPreventsFurtherMoves() {
        service.reject("job", "candidate", new RejectCandidateReq("a", "  Not a fit  ", true));
        assertTrue(application.isClosed());
        assertEquals(JobApplicationCloseType.REJECTED, application.getApplicationCloseType());
        assertEquals("Not a fit", application.getClosedReason());
        assertEquals(Boolean.TRUE, application.getConsiderForOtherOpenings());
        assertEquals(10L, application.getRecruitmentPipelineStage());
        assertEquals("Stage a", application.getClosedAtStageName());
        assertEquals("APPLIED", application.getClosedAtStageType());
        assertNotNull(application.getClosedAt());
        assertThrows(CustomException.class, () -> service.move("job", "candidate", new MoveCandidateReq("a", "b")));
        assertThrows(CustomException.class, () -> service.reject("job", "candidate", new RejectCandidateReq("a", null, false)));
        verify(applications, times(1)).save(application);
    }

    @Test
    void rejectsStaleRejectionWithoutModifyingApplication() {
        assertThrows(CustomException.class, () -> service.reject("job", "candidate", new RejectCandidateReq("b", null, false)));
        assertFalse(application.isClosed());
        verify(applications, never()).save(any());
    }

    @Test
    void showsRejectedCandidatesSeparatelyAndCountsOnlyActivePipelineCandidates() {
        JobPosition job = new JobPosition();
        job.setId(5L);
        job.setPublicId("job");
        when(positions.findByPublicIdAndTenantIdAndDeactivatedFalse("job", 2L)).thenReturn(Optional.of(job));
        JobApplication rejected = new JobApplication();
        rejected.setClosed(true);
        rejected.setApplicationCloseType(JobApplicationCloseType.REJECTED);
        rejected.setConsiderForOtherOpenings(false);
        rejected.setClosedReason("Experience");
        rejected.setClosedAtStageName("Original stage");
        rejected.setClosedAtStageType("SCREENING");
        rejected.setClosedAt(LocalDateTime.of(2026, 11, 20, 12, 0));
        rejected.setRecruitmentPipelineStage(null);
        when(applications.findByJobPositionIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(application, rejected));
        var result = service.board("job");
        assertEquals(2, result.getTotalApplications());
        assertEquals(1, result.getPipelineCount());
        assertEquals(1, result.getStages().get(0).getCandidates().size());
        assertEquals(1, result.getRejectedCandidates().size());
        assertEquals("Experience", result.getRejectedCandidates().get(0).getClosedReason());
        assertEquals(Boolean.FALSE, result.getRejectedCandidates().get(0).getConsiderForOtherOpenings());
        assertEquals("Original stage", result.getRejectedCandidates().get(0).getClosedAtStageName());
        assertEquals("SCREENING", result.getRejectedCandidates().get(0).getClosedAtStageType());
        assertEquals(rejected.getClosedAt(), result.getRejectedCandidates().get(0).getClosedAt());
        var json = new ObjectMapper().findAndRegisterModules()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .valueToTree(result).path("rejected_candidates").get(0);
        assertTrue(json.path("closed").asBoolean());
        assertEquals(0, json.path("application_close_type").asInt());
        assertEquals("Experience", json.path("closed_reason").asText());
        assertEquals("Original stage", json.path("closed_at_stage_name").asText());
        assertEquals("SCREENING", json.path("closed_at_stage_type").asText());
        assertTrue(json.hasNonNull("closed_at"));
        assertFalse(json.has("rejection_reason"));
    }

    @Test
    void doesNotMisclassifyOtherClosureTypesAsRejections() {
        JobPosition job = new JobPosition();
        job.setId(5L);
        when(positions.findByPublicIdAndTenantIdAndDeactivatedFalse("job", 2L)).thenReturn(Optional.of(job));
        application.setClosed(true);
        application.setApplicationCloseType(JobApplicationCloseType.OFFER_CANCELLED);
        when(applications.findByJobPositionIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(application));
        assertThrows(InternalServerException.class, () -> service.board("job"));
        assertThrows(CustomException.class, () -> service.move("job", "candidate", new MoveCandidateReq("a", "b")));
    }

    @Test
    void rejectsInaccessibleJobBeforeReadingCandidates() {
        when(positions.findForUpdate("other", 2L)).thenReturn(Optional.empty());
        assertThrows(InternalServerException.class, () -> service.move("other", "candidate", new MoveCandidateReq("a", "b")));
        verifyNoInteractions(applications);
    }

    @Test
    void includesApplicationCreationTimeOnBoard() throws Exception {
        JobPosition job = new JobPosition();
        job.setId(5L);
        job.setPublicId("job");
        when(positions.findByPublicIdAndTenantIdAndDeactivatedFalse("job", 2L)).thenReturn(Optional.of(job));
        when(applications.findByJobPositionIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(application));
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 18, 12, 0);
        application.setCreatedAt(createdAt);
        var result = service.board("job");
        assertEquals(createdAt, result.getStages().get(0).getCandidates().get(0).getCreatedAt());
        var mapper = new ObjectMapper().findAndRegisterModules()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        assertEquals("2026-11-18T12:00:00", mapper.readTree(mapper.writeValueAsString(result))
                .path("stages").get(0).path("candidates").get(0).path("created_at").asText());
        assertEquals("APPLIED", mapper.readTree(mapper.writeValueAsString(result))
                .path("stages").get(0).path("stage_type").asText());
    }

    @Test
    void distinguishesOptionalStageTypes() {
        JobPosition job = new JobPosition();
        job.setId(5L);
        when(positions.findByPublicIdAndTenantIdAndDeactivatedFalse("job", 2L)).thenReturn(Optional.of(job));
        var approval = stage(10L, "approval");
        var background = stage(11L, "background");
        var custom = stage(12L, "custom");
        approval.setRoundType(RecruitmentPipelineRoundType.OFFER_APPROVAL);
        background.setRoundType(RecruitmentPipelineRoundType.BACKGROUND_CHECK);
        custom.setRoundType(RecruitmentPipelineRoundType.CUSTOM);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(approval, background, custom));
        var result = service.board("job");
        assertEquals(List.of("OFFER_APPROVAL", "BACKGROUND_CHECK", "CUSTOM"),
                result.getStages().stream().map(stage -> stage.getStageType()).toList());
    }
}
