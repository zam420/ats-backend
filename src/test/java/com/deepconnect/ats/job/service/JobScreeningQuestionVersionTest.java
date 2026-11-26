package com.deepconnect.ats.job.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationPlanImpact;
import com.deepconnect.ats.common.service.EnumsResService;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRoundReqRes;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import com.deepconnect.ats.job.dto.JobPositionReq;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.job.repo.JobEvaluationPlanRoundRepository;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.repo.JobScreeningQuestionRepository;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.jobapplication.service.JobApplicationService;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRoundReqRes;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionReqRes;
import com.deepconnect.ats.screeningquestionsform.enums.ScreeningQuestionType;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionsFormService;
import com.deepconnect.ats.user.service.UserServiceV2;

class JobScreeningQuestionVersionTest {
    private final JobPositionRepository positions = mock(JobPositionRepository.class);
    private final JobScreeningQuestionRepository questions = mock(JobScreeningQuestionRepository.class);
    private final JobPositionService service = new JobPositionService(
            mock(EvaluationPlanService.class), mock(RecruitmentPipelineService.class),
            mock(JobPositionAccessService.class), mock(ScreeningQuestionsFormService.class),
            mock(JobApplicationService.class), mock(UserServiceV2.class), mock(EnumsResService.class),
            positions, mock(JobEvaluationPlanRoundRepository.class), mock(JobRecruitmentPipelineRoundRepository.class),
            questions, mock(JobApplicationRepository.class), mock(CandidateEvaluationPlanImpact.class));
    private final JobPosition job = new JobPosition();
    private final JobPositionReq request = new JobPositionReq();
    private final List<JobScreeningQuestion> savedQuestions = new ArrayList<>();

    @BeforeEach
    void setUp() {
        var user = new LoggedInUserDetails(2L, 1L, "user", "user@example.com", "user", false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        ReflectionTestUtils.setField(service, "maxRounds", 10);
        ReflectionTestUtils.setField(service, "maxQuestions", 40);
        ReflectionTestUtils.setField(service, "rtLabelMaxLength", 1000);
        ReflectionTestUtils.setField(service, "nonRTLabelMaxLength", 500);
        ReflectionTestUtils.setField(service, "shortDescriptionMaxWords", 70);
        job.setId(5L);
        job.setPublicId("job");
        job.setRecruitmentPipelineRounds(List.of());
        job.setEvaluationPlanRounds(List.of());
        job.setScreeningQuestions(List.of());
        request.setId("job");
        request.setDescription("Description");
        request.setRecruitmentPipelineRounds(List.of());
        request.setEvaluationPlanRounds(List.of());
        request.setScreeningQuestions(List.of());
        when(positions.findForUpdate("job", 2L)).thenReturn(Optional.of(job));
        when(positions.save(any(JobPosition.class))).thenAnswer(invocation -> {
            JobPosition position = invocation.getArgument(0);
            position.setId(5L);
            return position;
        });
        when(questions.saveAll(any())).thenAnswer(invocation -> {
            List<JobScreeningQuestion> saved = invocation.getArgument(0);
            savedQuestions.addAll(saved);
            return saved;
        });
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    private ScreeningQuestionReqRes question(String label, int order) {
        var question = new ScreeningQuestionReqRes();
        question.setLabel(label);
        question.setOrder(order);
        question.setScreeningQuestionType(ScreeningQuestionType.INPUT_BOX);
        return question;
    }

    @Test
    void createsFirstVersionForAllQuestions() {
        var evaluation = new EvaluationPlanRoundReqRes();
        evaluation.setName("Interview");
        evaluation.setRoundType(EvaluationPlanRoundType.HR);
        evaluation.setOrder(0);
        request.setEvaluationPlanRounds(List.of(evaluation));
        var stages = new ArrayList<RecruitmentPipelineRoundReqRes>();
        for (RecruitmentPipelineRoundType type : RecruitmentPipelineRoundType.values()) {
            if (!type.getIsCustom()) {
                var stage = new RecruitmentPipelineRoundReqRes();
                stage.setRoundType(type);
                stage.setName(type.name());
                stage.setOrder(stages.size());
                stages.add(stage);
            }
        }
        request.setRecruitmentPipelineRounds(stages);
        request.setScreeningQuestions(List.of(question("First", 0), question("Second", 1)));

        service.create(request);

        assertEquals(2, savedQuestions.size());
        assertTrue(savedQuestions.stream().allMatch(q -> q.getActive() && q.getQuestionSetVersion() == 1));
        verify(questions).findLatestQuestionSetVersion(5L);
    }

    @Test
    void changedSetUsesNextVersionAndArchivesPreviousQuestions() {
        job.setScreeningQuestions(List.of(JobScreeningQuestion.from(question("Old", 0), 5L)));
        request.setScreeningQuestions(List.of(question("New", 0), question("Another", 1)));
        when(questions.findLatestQuestionSetVersion(5L)).thenReturn(3);

        service.update(request);

        verify(questions).archiveByJobPositionId(5L);
        assertEquals(2, savedQuestions.size());
        assertTrue(savedQuestions.stream().allMatch(q -> q.getActive() && q.getQuestionSetVersion() == 4));
    }

    @Test
    void unchangedQuestionsDoNotCreateAnotherVersion() {
        var question = question("Same", 0);
        job.setScreeningQuestions(List.of(JobScreeningQuestion.from(question, 5L)));
        request.setScreeningQuestions(List.of(question));

        service.update(request);

        verify(questions, never()).archiveByJobPositionId(any());
        verify(questions, never()).findLatestQuestionSetVersion(any());
        verify(questions, never()).saveAll(any());
    }

    @Test
    void removingAndReaddingQuestionsContinuesAfterArchivedVersion() {
        job.setScreeningQuestions(List.of(JobScreeningQuestion.from(question("Old", 0), 5L)));
        service.update(request);
        verify(questions).archiveByJobPositionId(5L);
        verify(questions, never()).saveAll(any());

        job.setScreeningQuestions(List.of());
        request.setScreeningQuestions(List.of(question("New", 0)));
        when(questions.findLatestQuestionSetVersion(5L)).thenReturn(2);
        service.update(request);

        assertEquals(1, savedQuestions.size());
        assertEquals(3, savedQuestions.get(0).getQuestionSetVersion());
    }
}
