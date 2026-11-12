package com.deepconnect.ats.tenant.service;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.tenant.dto.TenantReq;
import com.deepconnect.ats.tenant.model.Tenant;
import com.deepconnect.ats.tenant.repo.TenantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TenantServiceLogoTest {
    private final TenantRepository repository = mock(TenantRepository.class);
    private final TenantLogoStorage logos = mock(TenantLogoStorage.class);
    private final TenantService service = new TenantService(repository, new TenantAccessService(), logos);
    private final LoggedInUserDetails user = new LoggedInUserDetails(
            1L, 2L, "user-id", "user@example.test", false, false, List.of());

    @Test
    void logoReadRequiresTenantAccess() {
        Tenant tenant = new Tenant();
        tenant.setOwnerId(3L);
        tenant.setLogoPath("logo.png");
        when(repository.findByDeactivatedFalseAndPublicId("tenant")).thenReturn(Optional.of(tenant));
        try (MockedStatic<AuthService> auth = Mockito.mockStatic(AuthService.class)) {
            auth.when(AuthService::getLoggedInUser).thenReturn(user);
            assertThrows(InternalServerException.class, () -> service.getLogo("tenant"));
            verifyNoInteractions(logos);
            tenant.setOwnerId(2L);
            when(logos.getPath("logo.png")).thenReturn(Path.of("logo.png"));
            assertEquals(Path.of("logo.png"), service.getLogo("tenant"));
        }
    }

    @Test
    void updateWithoutUploadKeepsExistingLogo() {
        Tenant tenant = new Tenant();
        tenant.setId(5L);
        tenant.setPublicId("tenant");
        tenant.setOwnerId(2L);
        tenant.setLogoPath("old-logo.png");
        TenantReq req = new TenantReq();
        req.setId("tenant");
        req.setName("New name");
        when(repository.findByDeactivatedFalseAndPublicId("tenant")).thenReturn(Optional.of(tenant));
        when(repository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));
        try (MockedStatic<AuthService> auth = Mockito.mockStatic(AuthService.class)) {
            auth.when(AuthService::getLoggedInUser).thenReturn(user);
            assertEquals("tenant", service.update(req, null));
        }
        assertEquals("old-logo.png", tenant.getLogoPath());
        verifyNoInteractions(logos);
    }

    @Test
    void detailUsesSnakeCaseLogoFlagWithoutExposingPath() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setLogoPath("secret-path.png");
        String json = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .writeValueAsString(com.deepconnect.ats.tenant.dto.TenantDetailsRes.from(tenant));
        assertTrue(json.contains("\"has_logo\":true"));
        assertFalse(json.contains("secret-path"));
    }
}
