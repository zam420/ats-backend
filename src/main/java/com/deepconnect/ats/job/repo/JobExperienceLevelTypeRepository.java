package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.model.JobExperienceLevelType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface JobExperienceLevelTypeRepository extends JpaRepository<JobExperienceLevelType, Integer> {
}
