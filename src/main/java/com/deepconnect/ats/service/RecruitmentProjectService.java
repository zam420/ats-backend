package com.deepconnect.ats.service;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.model.RecruitmentProject;
import com.deepconnect.ats.repository.RecruitmentProjectRepository;
import com.deepconnect.ats.service.auth.AuthService;

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
public class RecruitmentProjectService {

    private final RecruitmentProjectRepository projectRepository;

    public List<RecruitmentProject> listAll() {
        return projectRepository.findAll();
    }

    public List<RecruitmentProject> listActive() {
        return projectRepository.findByStatus(0);
    }

    public AtsPageRsp<RecruitmentProject> listProject(AtsPageQuery query) {
        Page<RecruitmentProject> page = projectRepository.findAll(query.toPageable());
        return AtsPageRsp.of(page);
    }

    public Optional<RecruitmentProject> getById(Long id) {
        return projectRepository.findById(id);
    }

    @Transactional
    public Long saveProject(RecruitmentProject req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        RecruitmentProject project;
        if (req.getId() != null) {
            project = projectRepository.findById(req.getId())
                    .orElseThrow(() -> new InternalServerException("Project not found: " + req.getId()));
        } else {
            project = new RecruitmentProject();
            project.setCreatedById(currentUserId);
        }

        if (req.getName() != null) project.setName(req.getName());
        if (req.getStatus() != null) project.setStatus(req.getStatus());
        if (req.getWorkLocation() != null) project.setWorkLocation(req.getWorkLocation());

        return projectRepository.save(project).getId();
    }

    @Transactional
    public void inactiveProject(Long projectId) {
        RecruitmentProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new InternalServerException("Project not found: " + projectId));
        project.setStatus(1); // 1 = inactive
        projectRepository.save(project);
    }
}
