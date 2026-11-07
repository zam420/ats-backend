package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.model.JobCandidateQuestionType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface JobCandidateQuestionTypeRepository extends JpaRepository<JobCandidateQuestionType, Integer> {
}
