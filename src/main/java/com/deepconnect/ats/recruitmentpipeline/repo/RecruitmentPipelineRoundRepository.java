package com.deepconnect.ats.recruitmentpipeline.repo;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitmentPipelineRoundRepository extends BaseRepository<RecruitmentPipelineRound, Long> {
    
    List<RecruitmentPipelineRound> findByRecruitmentPipelineIdOrderByOrderAsc(Long recruitmentPipelineId);

    @Modifying
    @Query(value = "delete from recruitment_pipeline_round where recruitment_pipeline=?1", nativeQuery = true)
    void deleteByRecruitmentPipelineId(Long recruitmentPipelineId);
}
