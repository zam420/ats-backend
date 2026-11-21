package com.deepconnect.ats.jobapplication.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.deepconnect.ats.jobapplication.model.JobApplication;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobPositionIdAndEmail(Long jobPositionId, String email);
    boolean existsByRecruitmentPipelineStageAndRejectedFalse(Long stageId);
    List<JobApplication> findByJobPositionIdOrderByCreatedAtDesc(Long jobPositionId);
    Optional<JobApplication> findByPublicIdAndJobPositionId(String publicId, Long jobPositionId);

    @Modifying
    @Query("update JobApplication a set a.recruitmentPipelineStage = null "
            + "where a.rejected = true and a.recruitmentPipelineStage in :stageIds")
    void clearRejectedStages(@Param("stageIds") Collection<Long> stageIds);

    interface PositionCount {
        Long getJobPositionId();
        long getApplicationCount();
    }

    @Query("select a.jobPositionId as jobPositionId, count(a) as applicationCount from JobApplication a "
            + "where a.jobPositionId in :ids group by a.jobPositionId")
    List<PositionCount> countForPositions(@Param("ids") Collection<Long> ids);
}
