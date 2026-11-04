package com.deepconnect.ats.recruitmentpipeline.repo;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RecruitmentPipelineRoundTypeRepository extends JpaRepository<RecruitmentPipelineRoundType, Integer> {
}
