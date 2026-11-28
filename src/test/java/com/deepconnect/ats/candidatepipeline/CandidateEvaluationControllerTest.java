package com.deepconnect.ats.candidatepipeline;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.deepconnect.ats.candidatepipeline.controller.CandidateEvaluationController;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationService;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

class CandidateEvaluationControllerTest {
    private final CandidateEvaluationService service = mock(CandidateEvaluationService.class);
    private MockMvc mvc;
    private final String base = "/api/pipeline/job/candidate/candidate/evaluation";

    @BeforeEach
    void setUp() {
        var mapper = new ObjectMapper().findAndRegisterModules()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new CandidateEvaluationController(service))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .setValidator(validator).build();
    }

    @Test
    void readsEvaluationsAtSingularPath() throws Exception {
        mvc.perform(get(base)).andExpect(status().isOk());
        verify(service).get("job", "candidate");
    }

    @Test
    void acceptsSnakeCaseScheduleAndNumericResult() throws Exception {
        mvc.perform(post(base + "/rounds/round/schedule").contentType(MediaType.APPLICATION_JSON)
                .content("{\"scheduled_at\":\"2026-11-25T10:00:00Z\",\"duration_minutes\":45}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        verify(service).schedule("job", "candidate", "round", Instant.parse("2026-11-25T10:00:00Z"), 45);
        mvc.perform(post(base + "/rounds/round/result").contentType(MediaType.APPLICATION_JSON)
                .content("{\"result\":1}")).andExpect(status().isOk());
        verify(service).recordResult("job", "candidate", "round", CandidateEvaluationResult.STRONG_NO);
        mvc.perform(post(base + "/rounds/round/result").contentType(MediaType.APPLICATION_JSON)
                .content("{\"result\":3}")).andExpect(status().isOk());
        verify(service).recordResult("job", "candidate", "round", CandidateEvaluationResult.YES);
        mvc.perform(post(base + "/rounds/round/result").contentType(MediaType.APPLICATION_JSON)
                .content("{\"result\":4}")).andExpect(status().isOk());
        verify(service).recordResult("job", "candidate", "round", CandidateEvaluationResult.NO);
    }

    @Test
    void validatesRoundRequests() throws Exception {
        mvc.perform(post(base + "/rounds").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\", \"round_type\":3, \"interviewers\":[]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post(base + "/rounds/round/schedule").contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isBadRequest());
        mvc.perform(post(base + "/rounds/round/schedule").contentType(MediaType.APPLICATION_JSON)
                .content("{\"scheduled_at\":\"2026-11-25T10:00:00Z\",\"duration_minutes\":0}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
        mvc.perform(post(base + "/rounds").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Extra\", \"round_type\":3, \"interviewers\":[\"user\"]}"))
                .andExpect(status().isOk());
        verify(service).addRound("job", "candidate", "Extra", null, EvaluationPlanRoundType.CUSTOM, List.of("user"));
    }

    @Test
    void statusIdsUseConflictFourAndCompletedFive() throws Exception {
        mvc.perform(post(base + "/rounds/round/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":3}")).andExpect(status().isOk());
        verify(service).changeStatus("job", "candidate", "round", CandidateEvaluationStatus.AWAITING_FEEDBACK);
        mvc.perform(post(base + "/rounds/round/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":4}")).andExpect(status().isOk());
        verify(service).changeStatus("job", "candidate", "round", CandidateEvaluationStatus.CONFLICTING_FEEDBACK);
        mvc.perform(post(base + "/rounds/round/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":5}")).andExpect(status().isOk());
        verify(service).changeStatus("job", "candidate", "round", CandidateEvaluationStatus.COMPLETED);
        mvc.perform(post(base + "/rounds/round/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":2}")).andExpect(status().isBadRequest());
        ObjectMapper mapper = new ObjectMapper();
        org.junit.jupiter.api.Assertions.assertEquals("4", mapper.writeValueAsString(CandidateEvaluationStatus.CONFLICTING_FEEDBACK));
        org.junit.jupiter.api.Assertions.assertEquals("5", mapper.writeValueAsString(CandidateEvaluationStatus.COMPLETED));
    }
}
