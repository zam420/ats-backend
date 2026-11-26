package com.deepconnect.ats.candidatepipeline;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.deepconnect.ats.candidatepipeline.controller.CandidateApplicationController;
import com.deepconnect.ats.candidatepipeline.service.CandidateApplicationService;

class CandidateApplicationControllerTest {
    @TempDir Path root;

    @Test
    void resumeIsAnUncacheableAttachment() throws Exception {
        var service = mock(CandidateApplicationService.class);
        Path file = Files.write(root.resolve("resume.pdf"), new byte[] {1, 2, 3});
        when(service.resume("job", "candidate")).thenReturn(new CandidateApplicationService.Resume(file, "CV.pdf"));
        var mvc = MockMvcBuilders.standaloneSetup(new CandidateApplicationController(service)).build();
        mvc.perform(get("/api/pipeline/job/candidates/candidate/resume"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(content().bytes(new byte[] {1, 2, 3}))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.startsWith("attachment;")))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }
}
