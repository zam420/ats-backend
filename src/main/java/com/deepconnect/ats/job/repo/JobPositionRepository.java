package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Long>, JpaSpecificationExecutor<JobPosition> {
    Optional<JobPosition> findByPublicId(String publicId);
    Optional<JobPosition> findByPublicIdAndTenantId(String publicId, Long tenantId);
    List<JobPosition> findByStatus(JobStatus status);
    Optional<JobPosition> findByTitle(String title);
}
