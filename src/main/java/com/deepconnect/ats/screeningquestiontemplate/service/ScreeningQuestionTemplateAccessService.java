package com.deepconnect.ats.screeningquestiontemplate.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.enums.PermissionType;
import com.deepconnect.ats.common.enums.AccessType;
import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestionTemplateShare;
import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningQuestionTemplateAccessService {
    
    /**
     * Determines the access type of a user for a given screening question template.
     * For users with appropriate permissions, this method will return the appropriate access type based on ownership, tenant-level access, and public visibility.
     * This is to ensure that the users with the same appropriate permissions can't access each other's data.
     * @param user the user whose access is being determined
     * @param template the screening question template for which access is being checked
     * @return the access type of the user for the given screening question template
     */
    public AccessType getAccessType(LoggedInUserDetails user, ScreeningQuestionTemplate template) {
        if(user.getUserId() == template.getOwnerId())
            return AccessType.WRITE;

        //user is not the owner of the plan
        if(user.getTenantId() == template.getTenantId()){
            if(user.hasPermission(PermissionType.SCREENING_QUESTION_TEMPLATE_ADMIN))
                return AccessType.TENANT_ADMIN;

            //user is not the owner and does not have admin permission
            if(template.getIsPublicForTenant())
                return AccessType.READ;

            //user is not the owner but plan is shared with him
            if(!CollectionUtils.isEmpty(template.getSharedWithUsers())) {
                for (ScreeningQuestionTemplateShare sharedWithUser : template.getSharedWithUsers()) {
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

    public void assertWriteAccess(LoggedInUserDetails loggedInUser, ScreeningQuestionTemplate template) {
        AccessType accessType = this.getAccessType(loggedInUser, template);
        if(accessType != AccessType.SYSTEM_ADMIN && accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE) {
            throw new InternalServerException("Not sufficient access to modify the screening question template: " + loggedInUser.getPublicId());
        }
    }

    public void assertReadAccess(LoggedInUserDetails loggedInUser, ScreeningQuestionTemplate template) {
        AccessType accessType = this.getAccessType(loggedInUser, template);
        if(accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE && accessType != AccessType.READ) {
            throw new InternalServerException("Not sufficient access to view the screening question template: " + loggedInUser.getPublicId());
        }
    }

    public boolean isUserInScreeningQuestionTemplateAdminRole(LoggedInUserDetails loggedInUser) {
        return loggedInUser.hasPermission(PermissionType.SCREENING_QUESTION_TEMPLATE_ADMIN);
    }

}
