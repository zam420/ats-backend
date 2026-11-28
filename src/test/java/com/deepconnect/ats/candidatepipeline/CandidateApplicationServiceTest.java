package com.deepconnect.ats.candidatepipeline;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.candidatepipeline.service.CandidateApplicationService;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.job.repo.JobScreeningQuestionRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.model.JobApplicationScreeningQuestionAnswer;
import com.deepconnect.ats.jobapplication.repo.JobApplicationAnswerRepository;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

class CandidateApplicationServiceTest {
    @TempDir Path root;
    private final JobPositionAccessService jobPositionAccessService = mock(JobPositionAccessService.class);
    private final JobApplicationRepository applications = mock(JobApplicationRepository.class);
    private final JobApplicationAnswerRepository answers = mock(JobApplicationAnswerRepository.class);
    private final JobScreeningQuestionRepository questions = mock(JobScreeningQuestionRepository.class);
    private final CandidateApplicationService service = new CandidateApplicationService(
            applications, answers, questions, jobPositionAccessService);
    private final JobApplication application = new JobApplication();

    @BeforeEach
    void setUp() {
        login("recruiter");
        ReflectionTestUtils.setField(service, "storageRoot", root.toString());
        var job = new JobPosition();
        job.setId(5L);
        job.setPublicId("job");
        job.setTenantId(2L);
        job.setTitle("Engineer");
        job.setRecruiters(List.of("recruiter"));
        job.setHiringManagers(List.of("manager"));
        application.setId(7L);
        application.setJobPosition(job);
        application.setPublicId("candidate");
        application.setEmail("candidate@example.com");
        application.setResumePath("job-jobApplicationRepository/12345678-1234-1234-1234-123456789abc.pdf");
        application.setResumeName("CV.pdf");
        when(applications.findForCandidateDetail("candidate", 2L)).thenReturn(Optional.of(application));
    }

    private void login(String id) {
        var user = new LoggedInUserDetails(2L, 1L, id, "user@example.com", "user name", false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void includesArchivedQuestionLabelsWithoutExposingStoragePath() {
        var question = new JobScreeningQuestion();
        question.setId(9L);
        question.setOrder(1);
        question.setLabel("Previous question");
        question.setActive(false);
        var answer = new JobApplicationScreeningQuestionAnswer();
        answer.setId(11L);
        answer.setJobScreeningQuestionId(9L);
        answer.setAnswer(List.of("Answer"));
        when(questions.findByJobPositionIdOrderByIdAsc(5L)).thenReturn(List.of(question));
        when(answers.findByJobApplicationId(7L)).thenReturn(List.of(answer));
        var result = service.get("candidate");
        assertEquals("Engineer", result.jobTitle());
        assertEquals("job", result.jobId());
        verify(jobPositionAccessService).assertReadAccess(any(LoggedInUserDetails.class), eq(application.getJobPosition()));
        assertEquals("Previous question", result.answers().get(0).question());
        assertEquals(List.of("Answer"), result.answers().get(0).values());
        assertEquals("CV.pdf", result.resumeName());
        var json = new ObjectMapper().findAndRegisterModules()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).valueToTree(result);
        assertFalse(json.has("resume_path"));
        assertFalse(json.has("candidate_user_id"));
        when(questions.findByJobPositionIdOrderByIdAsc(5L)).thenReturn(List.of());
        assertThrows(InternalServerException.class, () -> service.get("candidate"));
    }

    @Test
    void checksTenantAndJobAccessBeforeReadingPrivateData() {
        doThrow(new InternalServerException("Access denied")).when(jobPositionAccessService)
                .assertReadAccess(any(LoggedInUserDetails.class), eq(application.getJobPosition()));
        assertThrows(InternalServerException.class, () -> service.get("candidate"));
        assertThrows(InternalServerException.class, () -> service.resume("candidate"));
        verifyNoInteractions(answers, questions);
        assertThrows(CustomException.class, () -> service.get("other"));
        var otherTenant = new LoggedInUserDetails(3L, 1L, "recruiter", "user@example.com", "user name", false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(otherTenant, null, List.of()));
        assertThrows(CustomException.class, () -> service.get("candidate"));
        verify(applications).findForCandidateDetail("candidate", 3L);
        verify(jobPositionAccessService, times(2))
                .assertReadAccess(any(LoggedInUserDetails.class), eq(application.getJobPosition()));
        verifyNoInteractions(answers, questions);
    }

    @Test
    void servesOnlyTheStoredResumeWithinTheConfiguredDirectory() throws Exception {
        var folder = Files.createDirectory(root.resolve("job-jobApplicationRepository"));
        var file = Files.write(folder.resolve("12345678-1234-1234-1234-123456789abc.pdf"), new byte[] {1, 2});
        assertEquals(file.toRealPath(), service.resume("candidate").path());
        application.setResumePath("job-jobApplicationRepository/../../secret.pdf");
        assertThrows(InternalServerException.class, () -> service.resume("candidate"));
        application.setResumePath("job-jobApplicationRepository/12345678-1234-1234-1234-123456789abc.pdf");
        application.setResumeName("bad\r\nname.pdf");
        assertThrows(InternalServerException.class, () -> service.resume("candidate"));
    }
}
