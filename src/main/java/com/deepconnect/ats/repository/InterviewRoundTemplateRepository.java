package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.InterviewRoundTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRoundTemplateRepository extends JpaRepository<InterviewRoundTemplate, Long> {
    List<InterviewRoundTemplate> findByIptIdOrderByOrderAsc(Long iptId);
}
