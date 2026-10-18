package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.EmailTemplateSharing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailTemplateSharingRepository extends JpaRepository<EmailTemplateSharing, Long> {
    List<EmailTemplateSharing> findByShareToId(Long shareToId);
    List<EmailTemplateSharing> findByTemplateId(Long templateId);
}
