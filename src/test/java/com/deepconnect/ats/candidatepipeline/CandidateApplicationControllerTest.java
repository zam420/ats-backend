package com.deepconnect.ats.candidatepipeline;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.deepconnect.ats.candidatepipeline.controller.CandidateApplicationController;
import com.deepconnect.ats.candidatepipeline.controller.CandidatePipelineController;
import com.deepconnect.ats.candidate.controller.CandidateController;
import com.deepconnect.ats.candidate.dto.CandidateResMin;
import com.deepconnect.ats.candidate.service.CandidateService;
import com.deepconnect.ats.candidatepipeline.dto.MoveCandidateReq;
import com.deepconnect.ats.candidatepipeline.dto.RejectCandidateReq;
import com.deepconnect.ats.candidatepipeline.service.CandidateApplicationService;
import com.deepconnect.ats.candidatepipeline.service.CandidatePipelineService;
import com.deepconnect.ats.jobapplication.enums.JobApplicationCloseType;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

class CandidateApplicationControllerTest {
    @TempDir Path root;

    @Test
    void resumeIsAnUncacheableAttachment() throws Exception {
        var service = mock(CandidateApplicationService.class);
        Path file = Files.write(root.resolve("resume.pdf"), new byte[] {1, 2, 3});
        when(service.resume("job", "candidate")).thenReturn(new CandidateApplicationService.Resume(file, "CV.pdf"));
        var mvc = MockMvcBuilders.standaloneSetup(new CandidateApplicationController(service)).build();
        mvc.perform(get("/api/pipeline/job/candidate/candidate"))
                .andExpect(status().isOk());
        verify(service).get("job", "candidate");
        mvc.perform(get("/api/pipeline/job/candidate/candidate/resume"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(content().bytes(new byte[] {1, 2, 3}))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.startsWith("attachment;")))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    void moveAndRejectUseSingularCandidatePath() throws Exception {
        var service = mock(CandidatePipelineService.class);
        var mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        var mvc = MockMvcBuilders.standaloneSetup(new CandidatePipelineController(service))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper)).build();
        String base = "/api/pipeline/job/candidate/candidate";
        mvc.perform(post(base + "/move").contentType(MediaType.APPLICATION_JSON)
                .content("{\"from_stage_id\":\"first\",\"to_stage_id\":\"second\"}"))
                .andExpect(status().isOk());
        verify(service).move("job", "candidate", new MoveCandidateReq("first", "second"));
        mvc.perform(post(base + "/reject").contentType(MediaType.APPLICATION_JSON)
                .content("{\"stage_id\":\"first\",\"reason\":\"reason\",\"consider_for_other_openings\":true}"))
                .andExpect(status().isOk());
        verify(service).reject("job", "candidate", new RejectCandidateReq("first", "reason", true));
    }

    @Test
    void candidateListReturnsSwitcherNamesAndClosureWithoutExtraDetails() throws Exception {
        var application = new JobApplication();
        application.setPublicId("candidate");
        application.setFirstName("First");
        application.setLastName("Last");
        application.setPreferredName("Preferred");
        application.setClosed(true);
        application.setApplicationCloseType(JobApplicationCloseType.REJECTED);
        var service = mock(CandidateService.class);
        when(service.findAllAsList("job")).thenReturn(java.util.List.of(CandidateResMin.from(application)));
        var mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        var mvc = MockMvcBuilders.standaloneSetup(new CandidateController(service))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper)).build();
        mvc.perform(get("/api/candidate/list/job"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value("candidate"))
                .andExpect(jsonPath("$.result[0].name").value("Preferred Last"))
                .andExpect(jsonPath("$.result[0].application_close_type").value(0))
                .andExpect(jsonPath("$.result[0].email").doesNotExist());
        application.setPreferredName(null);
        application.setClosed(false);
        var dto = CandidateResMin.from(application);
        org.junit.jupiter.api.Assertions.assertEquals("First Last", dto.getName());
        org.junit.jupiter.api.Assertions.assertNull(dto.getApplicationCloseType());
    }
}
