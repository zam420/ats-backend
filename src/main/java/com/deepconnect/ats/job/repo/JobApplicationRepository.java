package com.deepconnect.ats.job.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deepconnect.ats.job.model.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobPositionIdAndEmail(Long jobPositionId, String email);
}
