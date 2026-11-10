package com.deepconnect.ats.recruitmentpipeline.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.enums.PermissionType;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.common.enums.AccessType;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineShare;

@Service
@RequiredArgsConstructor
public class RecruitmentPipelineAccessService {
    
    /**
     * Determines the access type of a user for a given recruitment pipeline.
     * For users with appropriate permissions, this method will return the appropriate access type based on ownership, tenant-level access, and public visibility.
     * This is to ensure that the users with the same appropriate permissions can't access each other's data.
     * @param user the user whose access is being determined
     * @param plan the recruitment pipeline for which access is being checked
     * @return the access type of the user for the given recruitment pipeline
     */
    public AccessType getAccessType(LoggedInUserDetails user, RecruitmentPipeline plan) {
        if(user.getUserId() != null && Objects.equals(user.getUserId(), plan.getOwnerId()))
            return AccessType.WRITE;

        //user is not the owner of the plan
        if(user.getTenantId() != null && Objects.equals(user.getTenantId(), plan.getTenantId())){
            if(user.hasPermission(PermissionType.RECRUITMENT_PIPELINE_TEMPLATE_ADMIN))
                return AccessType.TENANT_ADMIN;

            //user is not the owner and does not have admin permission
            if(plan.getIsPublicForTenant())
                return AccessType.READ;

            //user is not the owner but plan is shared with him
            if(!CollectionUtils.isEmpty(plan.getSharedWithUsers())) {
                for (RecruitmentPipelineShare sharedWithUser : plan.getSharedWithUsers()) {
                    if (sharedWithUser.getUserPublicId().equals(user.getPublicId())) {
                        return AccessType.READ;
                    }
                }
            }
        }

        //user is not the owner, does not have admin permission, and does not have tenant-level access
        if(plan.getIsPublicForAll()){
            return user.hasPermission(PermissionType.RECRUITMENT_PIPELINE_TEMPLATE_SYSTEM_ADMIN) ? AccessType.SYSTEM_ADMIN : AccessType.READ;
        }
            
        return AccessType.NONE;
    }

    public void assertWriteAccess(LoggedInUserDetails loggedInUser, RecruitmentPipeline plan) {
        AccessType accessType = this.getAccessType(loggedInUser, plan);
        if(accessType != AccessType.SYSTEM_ADMIN && accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE) {
            throw new InternalServerException("Not sufficient access to modify the recruitment pipeline: " + loggedInUser.getPublicId());
        }
    }

    public void assertReadAccess(LoggedInUserDetails loggedInUser, RecruitmentPipeline plan) {
        AccessType accessType = this.getAccessType(loggedInUser, plan);
        if(accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE && accessType != AccessType.READ) {
            throw new InternalServerException("Not sufficient access to view the recruitment pipeline: " + loggedInUser.getPublicId());
        }
    }

    public boolean isUserInRecruitmentPipelineAdminRole(LoggedInUserDetails loggedInUser) {
        return loggedInUser.hasPermission(PermissionType.RECRUITMENT_PIPELINE_TEMPLATE_ADMIN);
    }

}
