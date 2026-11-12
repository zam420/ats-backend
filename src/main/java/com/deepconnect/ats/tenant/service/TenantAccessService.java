package com.deepconnect.ats.tenant.service;

import org.springframework.util.CollectionUtils;
import java.util.Objects;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.rbac.enums.PermissionType;
import com.deepconnect.ats.tenant.model.Tenant;
import com.deepconnect.ats.common.enums.AccessType;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantAccessService {
    
    /**
     * Determines the access type of a user for a given tenant.
     * For users with appropriate permissions, this method will return the appropriate access type based on ownership and tenant-level access.
     * This is to ensure that the users with the same appropriate permissions can't access each other's data.
     * @param user the user whose access is being determined
     * @param plan the tenant for which access is being checked
     * @return the access type of the user for the given tenant
     */
    public AccessType getAccessType(LoggedInUserDetails user, Tenant tenant) {
        if(user.getUserId() != null){
            if(Objects.equals(user.getUserId(), tenant.getOwnerId()))
                return AccessType.WRITE;
            else if(user.hasPermission(PermissionType.TENANT_SYSTEM_ADMIN))
                return AccessType.SYSTEM_ADMIN;
        }

        return AccessType.NONE;
    }

    public void assertWriteAccess(LoggedInUserDetails loggedInUser, Tenant tenant) {
        AccessType accessType = this.getAccessType(loggedInUser, tenant);
        if(accessType != AccessType.SYSTEM_ADMIN && accessType != AccessType.WRITE) {
            throw new InternalServerException("Not sufficient access to modify the tenant: " + loggedInUser.getPublicId());
        }
    }

    public void assertReadAccess(LoggedInUserDetails loggedInUser, Tenant tenant) {
        this.assertWriteAccess(loggedInUser, tenant);
    }

    public boolean isUserInTenantSystemAdminRole(LoggedInUserDetails loggedInUser) {
        return loggedInUser.hasPermission(PermissionType.TENANT_SYSTEM_ADMIN);
    }

}
