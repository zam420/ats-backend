package com.deepconnect.ats.recruitmentproject.service;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.recruitmentproject.model.RecruitmentProject;
import com.deepconnect.ats.recruitmentproject.repo.RecruitmentProjectRepository;
import com.deepconnect.ats.recruitmentproject.dto.RecruitmentProjectReqRes;

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

    public AtsPageRsp<RecruitmentProjectReqRes> getAllByPage(AtsPageQuery query) {
        Page<RecruitmentProject> page = projectRepository.findByStatus(0, query.toPageable());
        return AtsPageRsp.of(page.map(RecruitmentProjectReqRes::from));
    }

    public Optional<RecruitmentProject> getById(Long id) {
        return projectRepository.findById(id);
    }

    private String save(RecruitmentProject recruitmentProject, RecruitmentProjectReqRes req) {
        recruitmentProject.setModifiedByUserId(AuthService.getLoggedInUserId());
        recruitmentProject.setName(req.getName());
        recruitmentProject.setProjectOwners(req.getProjectOwners());
        if (req.getStatus() == null) 
            recruitmentProject.setStatus(0);
        else
            recruitmentProject.setStatus(req.getStatus());

        return projectRepository.save(recruitmentProject).getPublicId();
    }

    @Transactional
    public String create(RecruitmentProjectReqRes req) {
        RecruitmentProject recruitmentProject = new RecruitmentProject();
        recruitmentProject.setCreatedByUserId(AuthService.getLoggedInUserId());
        return this.save(recruitmentProject, req);
    }

    @Transactional
    public String update(RecruitmentProjectReqRes req) {
        if (req.getId() == null)
            throw new InternalServerException("Invalid Project Id: " + req.getId());

        RecruitmentProject existingProject = projectRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Project not found: " + req.getId()));

        return this.save(existingProject, req);
    }

    @Transactional
    public void deactivate(String publicId, Integer status) {
        RecruitmentProject project = projectRepository.findByPublicId(publicId)
                .orElseThrow(() -> new InternalServerException("Project not found: " + publicId));
        project.setStatus(status);
        projectRepository.save(project);
    }

    // @Transactional(readOnly = true)
    // public Optional<EvaluationPlanDetailsRes> findByPublicId(String publicId) {
    //     return evaluationPlanRepository.findByPublicId(publicId).map(EvaluationPlanDetailsRes::from);
    // }
}
