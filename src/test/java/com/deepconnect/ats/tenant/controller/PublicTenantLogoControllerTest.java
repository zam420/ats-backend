package com.deepconnect.ats.tenant.controller;

import com.deepconnect.ats.tenant.service.TenantLogoStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PublicTenantLogoControllerTest {
    @TempDir Path root;

    @Test
    void servesLogoWithoutAuthenticationWithPublicCaching() throws Exception {
        Path directory = Files.createDirectories(root.resolve("tenant-logos"));
        String name = "12345678-1234-1234-1234-123456789abc.png";
        byte[] content = {1, 2, 3};
        Files.write(directory.resolve(name), content);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PublicTenantLogoController(new TenantLogoStorage(root.toString()))).build();

        mvc.perform(get("/api/public/tenant-logos/" + name))
            .andExpect(status().isOk())
            .andExpect(content().contentType("image/png"))
            .andExpect(content().bytes(content))
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("public")))
            .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("immutable")))
            .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }
}
