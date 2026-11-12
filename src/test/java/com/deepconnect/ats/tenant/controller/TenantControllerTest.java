package com.deepconnect.ats.tenant.controller;

import com.deepconnect.ats.tenant.dto.TenantReq;
import com.deepconnect.ats.tenant.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TenantControllerTest {
    @TempDir Path tempDir;
    private TenantService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(TenantService.class);
        mvc = MockMvcBuilders.standaloneSetup(new TenantController(service)).build();
    }

    @Test
    void existingJsonCreateRemainsSupported() throws Exception {
        when(service.create(any(TenantReq.class))).thenReturn("tenant-id");
        mvc.perform(post("/api/tenant/create").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Example\",\"description\":\"Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id", is("tenant-id")));
        verify(service).create(argThat(req -> req.getName().equals("Example") && req.getDescription().equals("Description")));
    }

    @Test
    void multipartCreateMapsJsonAndOptionalLogo() throws Exception {
        when(service.create(any(TenantReq.class), any())).thenReturn("tenant-id");
        MockMultipartFile tenant = new MockMultipartFile("tenant", "blob", "application/json",
                "{\"name\":\"Example\",\"description\":\"Description\"}".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile logo = new MockMultipartFile("logo", "logo.svg", "image/svg+xml", "<svg/>".getBytes(StandardCharsets.UTF_8));
        mvc.perform(multipart("/api/tenant/create").file(tenant).file(logo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id", is("tenant-id")));
        verify(service).create(argThat(req -> req.getName().equals("Example") && req.getDescription().equals("Description")), any());
    }

    @Test
    void multipartUpdateWithoutLogoPreservesExistingLogo() throws Exception {
        when(service.update(any(TenantReq.class), isNull())).thenReturn("tenant-id");
        MockMultipartFile tenant = new MockMultipartFile("tenant", "blob", "application/json",
                "{\"id\":\"tenant-id\",\"name\":\"Example\"}".getBytes(StandardCharsets.UTF_8));
        mvc.perform(multipart("/api/tenant/update").file(tenant))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id", is("tenant-id")));
        verify(service).update(argThat(req -> req.getId().equals("tenant-id")), isNull());
    }

    @Test
    void currentLogoUsesPrivateRevalidatingCacheAndUpdatesEtag() throws Exception {
        Path logo = tempDir.resolve("logo.svg");
        byte[] first = "<svg>first</svg>".getBytes(StandardCharsets.UTF_8);
        Files.write(logo, first);
        when(service.findCurrentLogo()).thenReturn(logo);

        var response = mvc.perform(get("/api/tenant/current/logo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/svg+xml"))
                .andExpect(content().bytes(first))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-cache, must-revalidate, private"))
                .andExpect(header().string(HttpHeaders.VARY, HttpHeaders.AUTHORIZATION))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Content-Security-Policy", "default-src 'none'; sandbox"))
                .andReturn().getResponse();
        String etag = response.getHeader(HttpHeaders.ETAG);
        assertNotNull(etag);

        mvc.perform(get("/api/tenant/current/logo").header(HttpHeaders.IF_NONE_MATCH, etag))
                .andExpect(status().isNotModified())
                .andExpect(content().string(""))
                .andExpect(header().string(HttpHeaders.ETAG, etag));
        Files.writeString(logo, "<svg>changed</svg>");
        mvc.perform(get("/api/tenant/current/logo").header(HttpHeaders.IF_NONE_MATCH, etag))
                .andExpect(status().isOk())
                .andExpect(content().string("<svg>changed</svg>"));
        verify(service, times(3)).findCurrentLogo();
    }
}
