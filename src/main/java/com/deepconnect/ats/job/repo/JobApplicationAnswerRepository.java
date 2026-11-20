package com.deepconnect.ats.job.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deepconnect.ats.job.model.JobApplicationScreeningQuestionAnswer;

public interface JobApplicationAnswerRepository extends JpaRepository<JobApplicationScreeningQuestionAnswer, Long> {
}
