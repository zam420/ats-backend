package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.InterviewProcessTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewProcessTemplateRepository extends JpaRepository<InterviewProcessTemplate, Long> {
    List<InterviewProcessTemplate> findByStatus(Integer status);
}
