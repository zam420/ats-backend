package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.model.JobEmploymentType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface JobEmploymentTypeRepository extends JpaRepository<JobEmploymentType, Integer> {
}
