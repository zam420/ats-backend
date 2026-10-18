package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long>, JpaSpecificationExecutor<EmailTemplate> {
    List<EmailTemplate> findByStatus(Integer status);
    List<EmailTemplate> findByCreateById(Long createById);
}
