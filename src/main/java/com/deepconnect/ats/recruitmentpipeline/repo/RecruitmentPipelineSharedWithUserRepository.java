package com.deepconnect.ats.recruitmentpipeline.repo;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineSharedWithUser;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitmentPipelineSharedWithUserRepository extends BaseRepository<RecruitmentPipelineSharedWithUser, Long> {
    
    List<RecruitmentPipelineSharedWithUser> findByRecruitmentPipelineIdOrderByIdAsc(Long recruitmentPipelineId);

    @Modifying
    @Query(value = "delete from recruitment_pipeline_shared_with_user where recruitment_pipeline=?1", nativeQuery = true)
    void deleteByRecruitmentPipelineId(Long recruitmentPipelineId);
}
