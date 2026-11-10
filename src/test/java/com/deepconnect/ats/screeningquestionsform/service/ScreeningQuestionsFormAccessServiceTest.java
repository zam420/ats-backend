package com.deepconnect.ats.screeningquestionsform.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.enums.AccessType;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsFormShare;

class ScreeningQuestionsFormAccessServiceTest {
    private final ScreeningQuestionsFormAccessService service = new ScreeningQuestionsFormAccessService();

    private LoggedInUserDetails user(long tenantId, long userId, String publicId, String... permissions) {
        return new LoggedInUserDetails(tenantId, userId, publicId, "user@example.test", false, false,
                java.util.Arrays.stream(permissions).map(SimpleGrantedAuthority::new).toList());
    }

    private ScreeningQuestionsForm form(long tenantId, long ownerId, boolean system) {
        ScreeningQuestionsForm form = new ScreeningQuestionsForm();
        form.setTenantId(tenantId);
        form.setOwnerId(ownerId);
        form.setIsPublicForAll(system);
        form.setIsPublicForTenant(false);
        return form;
    }

    @Test
    void ownershipAndTenantUseValueEquality() {
        ScreeningQuestionsForm form = form(1000L, 2000L, false);
        assertEquals(AccessType.WRITE, service.getAccessType(user(1000L, 2000L, "owner"), form));
        assertEquals(AccessType.TENANT_ADMIN, service.getAccessType(user(1000L, 3000L, "admin", "sqta"), form));
        assertEquals(AccessType.NONE, service.getAccessType(user(1001L, 3000L, "other", "sqta"), form));
    }

    @Test
    void sharedFormsOnlyGrantAccessToTheirRecipientInSameTenant() {
        ScreeningQuestionsForm form = form(1000L, 2000L, false);
        ScreeningQuestionsFormShare share = new ScreeningQuestionsFormShare();
        share.setUserPublicId("recipient");
        form.setSharedWithUsers(List.of(share));
        assertEquals(AccessType.READ, service.getAccessType(user(1000L, 3000L, "recipient"), form));
        assertEquals(AccessType.NONE, service.getAccessType(user(1000L, 4000L, "other"), form));
        assertEquals(AccessType.NONE, service.getAccessType(user(1001L, 3000L, "recipient"), form));
    }

    @Test
    void crossTenantSystemTemplatesAreReadableAndSystemAdminsCanWrite() {
        ScreeningQuestionsForm form = form(1000L, 2000L, true);
        LoggedInUserDetails reader = user(1001L, 3000L, "reader");
        LoggedInUserDetails admin = user(1001L, 4000L, "admin", "sqtsa");
        assertDoesNotThrow(() -> service.assertReadAccess(reader, form));
        assertThrows(InternalServerException.class, () -> service.assertWriteAccess(reader, form));
        assertDoesNotThrow(() -> service.assertReadAccess(admin, form));
        assertDoesNotThrow(() -> service.assertWriteAccess(admin, form));
    }
}
