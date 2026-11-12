package com.deepconnect.ats.tenant.service;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.tenant.dto.TenantReq;
import com.deepconnect.ats.tenant.model.Tenant;
import com.deepconnect.ats.tenant.enums.TenantType;
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
        when(repository.findByDeactivatedFalseAndPublicIdAndType("tenant", TenantType.CLIENT)).thenReturn(Optional.of(tenant));
        try (MockedStatic<AuthService> auth = Mockito.mockStatic(AuthService.class)) {
            auth.when(AuthService::getLoggedInUser).thenReturn(user);
            assertThrows(InternalServerException.class, () -> service.findLogoByDeactivatedFalseAndPublicId("tenant"));
            verifyNoInteractions(logos);
            tenant.setOwnerId(2L);
            when(logos.getPath("logo.png")).thenReturn(Path.of("logo.png"));
            assertEquals(Path.of("logo.png"), service.findLogoByDeactivatedFalseAndPublicId("tenant"));
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
        when(repository.findByDeactivatedFalseAndPublicIdAndType("tenant", TenantType.CLIENT)).thenReturn(Optional.of(tenant));
        when(repository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));
        try (MockedStatic<AuthService> auth = Mockito.mockStatic(AuthService.class)) {
            auth.when(AuthService::getLoggedInUser).thenReturn(user);
            assertEquals("tenant", service.update(req, null));
        }

        assertEquals("old-logo.png", tenant.getLogoPath());
        verifyNoInteractions(logos);
    }

    @Test
    void currentLogoUsesAuthenticatedUsersActiveTenant() {
        Tenant tenant = new Tenant();
        tenant.setOwnerId(99L);
        tenant.setLogoPath("current-logo.png");
        when(repository.findByDeactivatedFalseAndId(1L)).thenReturn(Optional.of(tenant));
        when(logos.getPath("current-logo.png")).thenReturn(Path.of("current-logo.png"));
        try (MockedStatic<AuthService> auth = Mockito.mockStatic(AuthService.class)) {
            auth.when(AuthService::getLoggedInUser).thenReturn(user);
            assertEquals(Path.of("current-logo.png"), service.findCurrentLogo());
        }
        verify(repository).findByDeactivatedFalseAndId(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void currentLogoFailsWhenTenantIsNotActiveOrMissing() {
        when(repository.findByDeactivatedFalseAndId(1L)).thenReturn(Optional.empty());
        try (MockedStatic<AuthService> auth = Mockito.mockStatic(AuthService.class)) {
            auth.when(AuthService::getLoggedInUser).thenReturn(user);
            assertThrows(InternalServerException.class, service::findCurrentLogo);
        }
        verifyNoInteractions(logos);
    }

    @Test
    void currentLogoPropagatesMissingLogoFailure() {
        Tenant tenant = new Tenant();
        when(repository.findByDeactivatedFalseAndId(1L)).thenReturn(Optional.of(tenant));
        when(logos.getPath(null)).thenThrow(new IllegalArgumentException("Tenant has no logo"));
        try (MockedStatic<AuthService> auth = Mockito.mockStatic(AuthService.class)) {
            auth.when(AuthService::getLoggedInUser).thenReturn(user);
            assertThrows(IllegalArgumentException.class, service::findCurrentLogo);
        }
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
