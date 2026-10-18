package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.RecruitmentProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitmentProjectRepository extends JpaRepository<RecruitmentProject, Long>, JpaSpecificationExecutor<RecruitmentProject> {
    Optional<RecruitmentProject> findByName(String name);
    List<RecruitmentProject> findByStatus(Integer status);
}
