package com.deepconnect.ats.job.service;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.rbac.enums.PermissionType;
import com.deepconnect.ats.common.enums.AccessType;
import com.deepconnect.ats.job.model.JobPosition;

@Service
@RequiredArgsConstructor
public class JobPositionAccessService {
    
    /**
     * Determines the access type of a user for a given recruitment pipeline.
     * For users with appropriate permissions, this method will return the appropriate access type based on ownership, tenant-level access, and public visibility.
     * This is to ensure that the users with the same appropriate permissions can't access each other's data.
     * @param user the user whose access is being determined
     * @param plan the recruitment pipeline for which access is being checked
     * @return the access type of the user for the given recruitment pipeline
     */
    public AccessType getAccessType(LoggedInUserDetails user, JobPosition jobPosition) {
        if(user.getUserId() == jobPosition.getOwnerId())
            return AccessType.WRITE;

        //user is not the owner of the plan
        if(user.getTenantId() == jobPosition.getTenantId()){
            if(user.hasPermission(PermissionType.RECRUITMENT_PIPELINE_TEMPLATE_ADMIN))
                return AccessType.TENANT_ADMIN;

            //user is one of recruiters or hiring managers for this job
            HashSet<String> hiringTeamPublicIds = new HashSet<>(jobPosition.getHiringManagers());
            hiringTeamPublicIds.addAll(jobPosition.getRecruiters());
            if(hiringTeamPublicIds.contains(user.getPublicId()))
                return AccessType.READ;
        }

        return AccessType.NONE;
    }

    public void assertWriteAccess(LoggedInUserDetails loggedInUser, JobPosition jobPosition) {
        AccessType accessType = this.getAccessType(loggedInUser, jobPosition);
        if(accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE) {
            throw new InternalServerException("Not sufficient access to modify the recruitment pipeline: " + loggedInUser.getPublicId());
        }
    }

    public void assertReadAccess(LoggedInUserDetails loggedInUser, JobPosition jobPosition) {
        AccessType accessType = this.getAccessType(loggedInUser, jobPosition);
        if(accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE && accessType != AccessType.READ) {
            throw new InternalServerException("Not sufficient access to view the recruitment pipeline: " + loggedInUser.getPublicId());
        }
    }

    public boolean isUserInJobPositionAdminRole(LoggedInUserDetails loggedInUser) {
        return loggedInUser.hasPermission(PermissionType.JOB_POSITION_ADMIN);
    }

}
