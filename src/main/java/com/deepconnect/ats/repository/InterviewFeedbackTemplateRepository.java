package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.InterviewFeedbackTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewFeedbackTemplateRepository extends JpaRepository<InterviewFeedbackTemplate, Long> {
}
