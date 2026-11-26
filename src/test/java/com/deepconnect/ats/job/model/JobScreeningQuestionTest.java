package com.deepconnect.ats.job.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionReqRes;

class JobScreeningQuestionTest {
    @Test
    void factoryMarksNewQuestionsActive() {
        JobScreeningQuestion question = JobScreeningQuestion.from(new ScreeningQuestionReqRes(), 1L);

        assertEquals(Boolean.TRUE, question.getActive());
    }
}
