package com.deepconnect.ats.job.security;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.screeningquestiontemplate.repo.ScreeningQuestionTypeRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import com.deepconnect.ats.job.dto.JobPositionReq;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.repo.JobEmploymentTypeRepository;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobWorkArrangementTypeRepository;
import com.deepconnect.ats.job.service.JobPositionService;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import com.deepconnect.ats.user.service.UserServiceV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class JobRichTextWriteTest {
    private final EvaluationPlanService plans = mock(EvaluationPlanService.class);
    private final RecruitmentPipelineService pipelines = mock(RecruitmentPipelineService.class);
    private final JobPositionRepository positions = mock(JobPositionRepository.class);
    private final JobPositionService service = new JobPositionService(plans, pipelines,
            mock(JobPositionAccessService.class), mock(UserServiceV2.class), positions, mock(JobEmploymentTypeRepository.class),
            mock(JobWorkArrangementTypeRepository.class),  mock(ScreeningQuestionTypeRepository.class));

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createAndUpdateBothSanitizeAllRichTextFields() {
        LoggedInUserDetails user = new LoggedInUserDetails(1L, 2L, "user", "user@example.com",
                false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of()));
        EvaluationPlan plan = new EvaluationPlan();
        plan.setId(3L);
        RecruitmentPipeline pipeline = new RecruitmentPipeline();
        pipeline.setId(4L);
        when(plans.findByPublicId("plan")).thenReturn(Optional.of(plan));
        when(pipelines.findByPublicId("pipeline")).thenReturn(Optional.of(pipeline));
        when(positions.save(any(JobPosition.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(positions.findByPublicId(eq("job"))).thenReturn(Optional.of(new JobPosition()));

        JobPositionReq req = new JobPositionReq();
        req.setId("job");
        req.setEvaluationPlan("plan");
        req.setRecruitmentPipeline("pipeline");
        req.setDescription("<p onclick=\"alert(1)\">Description</p>");
        req.setMandatoryRequirements("<script>alert(1)</script><p>Mandatory</p>");
        req.setPreferredRequirements("<img src=x onerror=alert(1)><p>Preferred</p>");

        service.create(req);
        service.update(req);

        ArgumentCaptor<JobPosition> saved = ArgumentCaptor.forClass(JobPosition.class);
        verify(positions, times(2)).save(saved.capture());
        for (JobPosition position : saved.getAllValues()) {
            assertEquals("<p>Description</p>", position.getDescription());
            assertEquals("<p>Mandatory</p>", position.getMandatoryRequirements());
            assertEquals("<p>Preferred</p>", position.getPreferredRequirements());
        }
    }
}
