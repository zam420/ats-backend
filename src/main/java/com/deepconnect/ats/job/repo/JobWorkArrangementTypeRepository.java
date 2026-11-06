package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.model.JobWorkArrangementType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface JobWorkArrangementTypeRepository extends JpaRepository<JobWorkArrangementType, Integer> {
}
