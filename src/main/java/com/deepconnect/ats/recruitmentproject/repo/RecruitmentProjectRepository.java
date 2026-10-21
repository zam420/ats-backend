package com.deepconnect.ats.recruitmentproject.repo;

import com.deepconnect.ats.recruitmentproject.model.RecruitmentProject;
import com.deepconnect.ats.common.repo.BaseRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitmentProjectRepository extends BaseRepository<RecruitmentProject, Long>, JpaSpecificationExecutor<RecruitmentProject> {
    Optional<RecruitmentProject> findByName(String name);
    
    
    // Add Pageable parameter and return Page<RecruitmentProject>
    Page<RecruitmentProject> findByStatus(Integer status, Pageable pageable);

}
