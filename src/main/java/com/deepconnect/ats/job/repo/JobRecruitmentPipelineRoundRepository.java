package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;

import com.deepconnect.ats.recruitmentpipeline.repo.RecruitmentPipelineRoundBaseRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRecruitmentPipelineRoundRepository extends RecruitmentPipelineRoundBaseRepository<JobRecruitmentPipelineRound> {
    
    List<JobRecruitmentPipelineRound> findByJobPositionIdOrderByOrderAsc(Long jobPositionId);

    @Modifying
    @Query(value = "delete from job_recruitment_pipeline_round where job_position=?1", nativeQuery = true)
    void deleteByJobPositionId(Long jobPositionId);
    
}
