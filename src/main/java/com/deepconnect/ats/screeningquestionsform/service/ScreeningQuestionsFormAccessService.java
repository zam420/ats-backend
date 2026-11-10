package com.deepconnect.ats.screeningquestionsform.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.enums.PermissionType;
import com.deepconnect.ats.common.enums.AccessType;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsFormShare;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningQuestionsFormAccessService {
    
    /**
     * Determines the access type of a user for a given screening questions form.
     * For users with appropriate permissions, this method will return the appropriate access type based on ownership, tenant-level access, and public visibility.
     * This is to ensure that the users with the same appropriate permissions can't access each other's data.
     * @param user the user whose access is being determined
     * @param template the screening questions form for which access is being checked
     * @return the access type of the user for the given screening questions form
     */
    public AccessType getAccessType(LoggedInUserDetails user, ScreeningQuestionsForm template) {
        if(user.getUserId() != null && Objects.equals(user.getUserId(), template.getOwnerId()))
            return AccessType.WRITE;

        //user is not the owner of the plan
        if(user.getTenantId() != null && Objects.equals(user.getTenantId(), template.getTenantId())){
            if(user.hasPermission(PermissionType.SCREENING_QUESTION_TEMPLATE_ADMIN))
                return AccessType.TENANT_ADMIN;

            //user is not the owner and does not have admin permission
            if(template.getIsPublicForTenant())
                return AccessType.READ;

            //user is not the owner but plan is shared with him
            if(!CollectionUtils.isEmpty(template.getSharedWithUsers())) {
                for (ScreeningQuestionsFormShare sharedWithUser : template.getSharedWithUsers()) {
                    if (sharedWithUser.getUserPublicId().equals(user.getPublicId())) {
                        return AccessType.READ;
                    }
                }
            }
        }

        //user is not the owner, does not have admin permission, and does not have tenant-level access
        if(template.getIsPublicForAll()){
            return user.hasPermission(PermissionType.SCREENING_QUESTION_TEMPLATE_SYSTEM_ADMIN) ? AccessType.SYSTEM_ADMIN : AccessType.READ;
        }
            
        return AccessType.NONE;
    }

    public void assertWriteAccess(LoggedInUserDetails loggedInUser, ScreeningQuestionsForm template) {
        AccessType accessType = this.getAccessType(loggedInUser, template);
        if(accessType != AccessType.SYSTEM_ADMIN && accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE) {
            throw new InternalServerException("Not sufficient access to modify the screening questions form: " + loggedInUser.getPublicId());
        }
    }

    public void assertReadAccess(LoggedInUserDetails loggedInUser, ScreeningQuestionsForm template) {
        AccessType accessType = this.getAccessType(loggedInUser, template);
        if(accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE && accessType != AccessType.READ) {
            throw new InternalServerException("Not sufficient access to view the screening questions form: " + loggedInUser.getPublicId());
        }
    }

    public boolean isUserInScreeningQuestionsFormAdminRole(LoggedInUserDetails loggedInUser) {
        return loggedInUser.hasPermission(PermissionType.SCREENING_QUESTION_TEMPLATE_ADMIN);
    }

}
