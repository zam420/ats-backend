package com.deepconnect.ats.tenant.controller;

import com.deepconnect.ats.tenant.service.TenantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrentLogoSecurityTest {
    @TempDir Path tempDir;

    @Configuration
    @EnableMethodSecurity
    static class Config {
        @Bean TenantService tenantService() { return mock(TenantService.class); }
        @Bean TenantController tenantController(TenantService service) { return new TenantController(service); }
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void currentLogoRequiresSamePermissionAsCurrentTenant() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class)) {
            TenantService service = context.getBean(TenantService.class);
            TenantController controller = context.getBean(TenantController.class);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("user", "n/a", List.of()));
            assertThrows(AccessDeniedException.class, controller::currentLogo);
            verifyNoInteractions(service);

            Path logo = tempDir.resolve("logo.png");
            Files.write(logo, new byte[] {1, 2, 3});
            when(service.findCurrentLogo()).thenReturn(logo);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    "user", "n/a", List.of(new SimpleGrantedAuthority("clns"))));
            assertArrayEquals(new byte[] {1, 2, 3}, controller.currentLogo().getBody());
            verify(service).findCurrentLogo();
        }
    }
}
