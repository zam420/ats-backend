package com.deepconnect.ats.jobapplication.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deepconnect.ats.jobapplication.model.JobApplicationScreeningQuestionAnswer;

public interface JobApplicationAnswerRepository extends JpaRepository<JobApplicationScreeningQuestionAnswer, Long> {
}
