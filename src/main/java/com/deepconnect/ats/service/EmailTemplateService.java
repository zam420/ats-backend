package com.deepconnect.ats.service;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.model.EmailTemplate;
import com.deepconnect.ats.model.EmailTemplateSharing;
import com.deepconnect.ats.repository.EmailTemplateRepository;
import com.deepconnect.ats.repository.EmailTemplateSharingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailTemplateSharingRepository sharingRepository;

    public AtsPageRsp<EmailTemplate> listTemplates(AtsPageQuery query) {
        Page<EmailTemplate> page = emailTemplateRepository.findAll(query.toPageable());
        return AtsPageRsp.of(page);
    }

    public List<EmailTemplate> listActive() {
        return emailTemplateRepository.findByStatus(0);
    }

    public Optional<EmailTemplate> getById(Long id) {
        return emailTemplateRepository.findById(id);
    }

    @Transactional
    public Long save(EmailTemplate template) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        EmailTemplate entity;
        if (template.getId() != null) {
            entity = emailTemplateRepository.findById(template.getId())
                    .orElseThrow(() -> new InternalServerException("Email template not found: " + template.getId()));
            entity.setLastModifiedById(currentUserId);
        } else {
            entity = new EmailTemplate();
            entity.setCreateById(currentUserId);
            entity.setLastModifiedById(currentUserId);
        }

        if (template.getName() != null) entity.setName(template.getName());
        if (template.getSubject() != null) entity.setSubject(template.getSubject());
        if (template.getContent() != null) entity.setContent(template.getContent());
        if (template.getRecruitmentType() != null) entity.setRecruitmentType(template.getRecruitmentType());
        if (template.getWorkLocation() != null) entity.setWorkLocation(template.getWorkLocation());
        if (template.getNotes() != null) entity.setNotes(template.getNotes());
        if (template.getStatus() != null) entity.setStatus(template.getStatus());
        else if (entity.getStatus() == null) entity.setStatus(0);
        if (template.getFields() != null) entity.setFields(template.getFields());

        return emailTemplateRepository.save(entity).getId();
    }

    @Transactional
    public void changeStatus(Long id, Integer status) {
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new InternalServerException("Email template not found: " + id));
        template.setStatus(status);
        emailTemplateRepository.save(template);
    }

    @Transactional
    public void saveSharing(Long templateId, List<Long> toUserIds) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        if (toUserIds == null) return;
        for (Long toUserId : toUserIds) {
            sharingRepository.save(EmailTemplateSharing.builder()
                    .templateId(templateId)
                    .shareFromId(currentUserId)
                    .shareToId(toUserId)
                    .status(0)
                    .build());
        }
    }
}
