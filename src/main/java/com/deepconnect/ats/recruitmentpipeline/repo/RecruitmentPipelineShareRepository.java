package com.deepconnect.ats.recruitmentpipeline.repo;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineShare;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitmentPipelineShareRepository extends BaseRepository<RecruitmentPipelineShare, Long> {
    
    List<RecruitmentPipelineShare> findByRecruitmentPipelineIdOrderByIdAsc(Long recruitmentPipelineId);

    @Modifying
    @Query(value = "delete from recruitment_pipeline_share where recruitment_pipeline=?1", nativeQuery = true)
    void deleteByRecruitmentPipelineId(Long recruitmentPipelineId);
}
