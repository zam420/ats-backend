package com.deepconnect.ats.jobapplication.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.jobapplication.dto.JobApplicationReq;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.repo.JobApplicationAnswerRepository;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;

class CandidatePipelineApplicationTest {
    private final JobPositionRepository positions = mock(JobPositionRepository.class);
    private final JobRecruitmentPipelineRoundRepository stages = mock(JobRecruitmentPipelineRoundRepository.class);
    private final JobApplicationRepository applications = mock(JobApplicationRepository.class);
    private final JobApplicationService service = new JobApplicationService(positions, applications,
            mock(JobApplicationAnswerRepository.class), stages);
    private final JobApplicationReq request = new JobApplicationReq();

    @BeforeEach
    void setUp(@TempDir Path temp) {
        ReflectionTestUtils.setField(service, "storageRoot", temp.toString());
        ReflectionTestUtils.setField(service, "maxResumeSizeBytes", 5L * 1024 * 1024);
        var user = new LoggedInUserDetails(2L, 1L, "user", "user@example.com", "user name", false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        TransactionSynchronizationManager.initSynchronization();
        JobPosition job = new JobPosition();
        job.setId(5L);
        job.setStatus(JobStatus.OPEN);
        job.setScreeningQuestions(List.of());
        when(positions.findForApplication("job")).thenReturn(Optional.of(job));
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail("ada@example.com");
        request.setCountryCode("+1");
        request.setPhoneNumber("123");
        request.setCurrentLocation("City");
        request.setAnswers(List.of());
        when(applications.saveAndFlush(any(JobApplication.class))).thenAnswer(invocation -> {
            JobApplication application = invocation.getArgument(0);
            application.setId(7L);
            return application;
        });
    }

    @AfterEach
    void clear() {
        TransactionSynchronizationManager.clearSynchronization();
        SecurityContextHolder.clearContext();
    }

    @Test
    void assignsNewApplicationToAppliedStage() {
        var stage = new JobRecruitmentPipelineRound();
        stage.setId(10L);
        stage.setRoundType(RecruitmentPipelineRoundType.APPLIED);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(stage));
        service.submit("job", request, new MockMultipartFile("resume", "resume.pdf", "application/pdf", new byte[] {1}));
        verify(applications).saveAndFlush(argThat(application -> application.getRecruitmentPipelineStage().equals(10L)));
    }

    @Test
    void rejectsPipelineWithoutAppliedAsFirstStage() {
        var stage = new JobRecruitmentPipelineRound();
        stage.setRoundType(RecruitmentPipelineRoundType.SCREENING);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(stage));
        assertThrows(CustomException.class, () -> service.submit("job", request, null));
        verify(applications, never()).saveAndFlush(any());
    }

    @Test
    void enforcesConfiguredResumeSizeLimit() {
        ReflectionTestUtils.setField(service, "maxResumeSizeBytes", 2L);
        var stage = new JobRecruitmentPipelineRound();
        stage.setRoundType(RecruitmentPipelineRoundType.APPLIED);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(stage));
        var tooLarge = new MockMultipartFile("resume", "resume.pdf", "application/pdf", new byte[] {1, 2, 3});
        assertThrows(CustomException.class, () -> service.submit("job", request, tooLarge));
        verify(applications, never()).saveAndFlush(any());

        var atLimit = new MockMultipartFile("resume", "resume.pdf", "application/pdf", new byte[] {1, 2});
        service.submit("job", request, atLimit);
        verify(applications).saveAndFlush(any(JobApplication.class));
    }
}
