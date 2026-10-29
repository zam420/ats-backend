package com.deepconnect.ats.evaluationplan.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.enums.PermissionType;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.common.enums.AccessType;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationPlanAccessService {
    
    /**
     * Determines the access type of a user for a given evaluation plan.
     * For users with appropriate permissions, this method will return the appropriate access type based on ownership, tenant-level access, and public visibility.
     * This is to ensure that the users with the same appropriate permissions can't access each other's data.
     * @param user the user whose access is being determined
     * @param plan the evaluation plan for which access is being checked
     * @return the access type of the user for the given evaluation plan
     */
    public AccessType getAccessType(LoggedInUserDetails user, EvaluationPlan plan) {
        if(user.getUserId() == plan.getOwnerId())
            return AccessType.WRITE;

        //user is not the owner of the plan
        if(user.getTenantId() == plan.getTenantId()){
            if(user.hasPermission(PermissionType.INTERVIEW_PLAN_TEMPLATE_ADMIN))
                return AccessType.TENANT_ADMIN;

            //user is not the owner and does not have admin permission
            if(plan.getIsPublicForTenant())
                return AccessType.READ;

            //TODO: implement feature that if template is shared with someone then that someone should have read access as well
        }

        //user is not the owner, does not have admin permission, and does not have tenant-level access
        if(plan.getIsPublicForAll()){
            return user.hasPermission(PermissionType.INTERVIEW_PLAN_TEMPLATE_SYSTEM_ADMIN) ? AccessType.SYSTEM_ADMIN : AccessType.READ;
        }
            
        return AccessType.NONE;
    }

    public void assertWriteAccess(LoggedInUserDetails loggedInUser, EvaluationPlan plan) {
        AccessType accessType = this.getAccessType(loggedInUser, plan);
        if(accessType != AccessType.SYSTEM_ADMIN && accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE) {
            throw new InternalServerException("Not sufficient access to modify the evaluation plan: " + loggedInUser.getPublicId());
        }
    }

    public void assertReadAccess(LoggedInUserDetails loggedInUser, EvaluationPlan plan) {
        AccessType accessType = this.getAccessType(loggedInUser, plan);
        if(accessType != AccessType.TENANT_ADMIN && accessType != AccessType.WRITE && accessType != AccessType.READ) {
            throw new InternalServerException("Not sufficient access to view the evaluation plan: " + loggedInUser.getPublicId());
        }
    }

    public boolean isUserInInterviewPlanAdminRole(LoggedInUserDetails loggedInUser) {
        return loggedInUser.hasPermission(PermissionType.INTERVIEW_PLAN_TEMPLATE_ADMIN);
    }

}
