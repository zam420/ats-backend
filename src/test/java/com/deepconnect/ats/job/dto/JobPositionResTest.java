package com.deepconnect.ats.job.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

import java.util.List;

import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.screeningquestionsform.enums.ScreeningQuestionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.Test;

class JobPositionResTest {
    @Test
    void includesHiringManagersAsPublicIdsInResponse() {
        JobPosition position = new JobPosition();
        position.setHiringManagers(List.of("manager1", "manager2"));

        JobPositionRes response = JobPositionRes.from(position);
        assertEquals(List.of("manager1", "manager2"), response.getHiringManagers());
        ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        assertEquals("manager1", mapper.valueToTree(response).get("hiring_managers").get(0).asText());
    }

    @Test
    void nestedJobDataSerializesWithoutAccessingLazyTypes() {
        var recruitment = new JobRecruitmentPipelineRound();
        recruitment.setId(11L);
        recruitment.setRoundType(RecruitmentPipelineRoundType.SCREENING);
        recruitment.setName("Screen");
        recruitment.setOrder(0);
        var evaluation = new JobEvaluationPlanRound();
        evaluation.setRoundType(EvaluationPlanRoundType.EXECUTIVE);
        evaluation.setInterviewers(List.of("interviewer"));
        evaluation.setOrder(1);
        var question = new JobScreeningQuestion();
        question.setLabel("Question");
        question.setScreeningQuestionType(ScreeningQuestionType.CHECKBOX);
        question.setIsMandatory(true);
        question.setOptions(List.of("Yes"));
        question.setOrder(2);
        var position = new JobPosition();
        position.setRecruitmentPipelineRounds(List.of(recruitment));
        position.setEvaluationPlanRounds(List.of(evaluation));
        position.setScreeningQuestions(List.of(question));

        var mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        var result = mapper.valueToTree(JobPositionRes.from(position));
        var recruitmentJson = result.get("recruitment_pipeline_rounds").get(0);
        var evaluationJson = result.get("evaluation_plan_rounds").get(0);
        var questionJson = result.get("screening_questions").get(0);
        assertEquals(11, recruitmentJson.get("id").asInt());
        assertEquals(1, recruitmentJson.get("round_type").asInt());
        assertEquals("Screen", recruitmentJson.get("name").asText());
        assertEquals(2, evaluationJson.get("round_type").asInt());
        assertEquals("interviewer", evaluationJson.get("interviewers").get(0).asText());
        assertEquals(3, questionJson.get("screening_question_type_id").asInt());
        assertEquals("Yes", questionJson.get("options").get(0).asText());
        assertEquals(true, questionJson.get("is_mandatory").asBoolean());
        assertFalse(recruitmentJson.has("recruitment_pipeline_round_type"));
        assertFalse(evaluationJson.has("evaluation_plan_round_type"));
        assertFalse(questionJson.has("screening_question_type"));
        //verifyNoInteractions(recruitmentType, evaluationType, questionType);
    }
}
