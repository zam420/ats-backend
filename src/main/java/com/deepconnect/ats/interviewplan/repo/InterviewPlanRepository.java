package com.deepconnect.ats.interviewplan.repo;

import com.deepconnect.ats.interviewplan.model.InterviewPlan;
import com.deepconnect.ats.common.repo.BaseWithTenantRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface InterviewPlanRepository extends BaseWithTenantRepository<InterviewPlan, Long> {
    
    Page<InterviewPlan> findByDeactivatedFalse(Pageable pageable);

    Page<InterviewPlan> findByDeactivatedFalseAndTenantId(Long tenantId, Pageable pageable);

    Page<InterviewPlan> findByDeactivatedFalseAndTenantIdAndOwnerId(Long tenantId, Long ownerId, Pageable pageable);

    Page<InterviewPlan> findByDeactivatedFalseAndIsPublicForAllTrue(Pageable pageable);

    Page<InterviewPlan> findByDeactivatedFalseAndIsPublicForTenantTrue(Pageable pageable);
    
}
