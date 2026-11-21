package com.deepconnect.ats.jobapplication.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deepconnect.ats.jobapplication.model.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobPositionIdAndEmail(Long jobPositionId, String email);
}
