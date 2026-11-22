package com.deepconnect.ats.jobapplication.service;

import static org.junit.jupiter.api.Assertions.*;
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
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.jobapplication.model.JobApplication;
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
    private final CandidatePipelineService service = new CandidatePipelineService(positions, stages, applications, access);
    private final JobApplication application = new JobApplication();

    @BeforeEach
    void setUp() {
        var user = new LoggedInUserDetails(2L, 1L, "user", "user@example.com", false, false, List.of());
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
    void recordsRejectionAndPreventsFurtherMoves() {
        service.reject("job", "candidate", new RejectCandidateReq("a", "  Not a fit  ", true));
        assertTrue(application.isRejected());
        assertEquals("Not a fit", application.getRejectionReason());
        assertEquals(Boolean.TRUE, application.getConsiderForOtherOpenings());
        assertEquals(10L, application.getRecruitmentPipelineStage());
        assertEquals("Stage a", application.getRejectedStageName());
        assertEquals("APPLIED", application.getRejectedStageType());
        assertNotNull(application.getRejectedAt());
        assertThrows(CustomException.class, () -> service.move("job", "candidate", new MoveCandidateReq("a", "b")));
        assertThrows(CustomException.class, () -> service.reject("job", "candidate", new RejectCandidateReq("a", null, false)));
        verify(applications, times(1)).save(application);
    }

    @Test
    void rejectsStaleRejectionWithoutModifyingApplication() {
        assertThrows(CustomException.class, () -> service.reject("job", "candidate", new RejectCandidateReq("b", null, false)));
        assertFalse(application.isRejected());
        verify(applications, never()).save(any());
    }

    @Test
    void showsRejectedCandidatesSeparatelyAndCountsOnlyActivePipelineCandidates() {
        JobPosition job = new JobPosition();
        job.setId(5L);
        job.setPublicId("job");
        when(positions.findByPublicIdAndTenantIdAndDeactivatedFalse("job", 2L)).thenReturn(Optional.of(job));
        JobApplication rejected = new JobApplication();
        rejected.setRejected(true);
        rejected.setConsiderForOtherOpenings(false);
        rejected.setRejectionReason("Experience");
        rejected.setRejectedStageName("Original stage");
        rejected.setRejectedStageType("SCREENING");
        rejected.setRejectedAt(LocalDateTime.of(2026, 11, 20, 12, 0));
        rejected.setRecruitmentPipelineStage(null);
        when(applications.findByJobPositionIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(application, rejected));
        var result = service.board("job");
        assertEquals(2, result.totalApplications());
        assertEquals(1, result.pipelineCount());
        assertEquals(1, result.stages().get(0).candidates().size());
        assertEquals(1, result.rejectedCandidates().size());
        assertEquals("Experience", result.rejectedCandidates().get(0).rejectionReason());
        assertEquals(Boolean.FALSE, result.rejectedCandidates().get(0).considerForOtherOpenings());
        assertEquals("Original stage", result.rejectedCandidates().get(0).rejectedStageName());
        assertEquals("SCREENING", result.rejectedCandidates().get(0).rejectedStageType());
        assertEquals(rejected.getRejectedAt(), result.rejectedCandidates().get(0).rejectedAt());
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
        assertEquals(createdAt, result.stages().get(0).candidates().get(0).createdAt());
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
                result.stages().stream().map(stage -> stage.stageType()).toList());
    }
}
