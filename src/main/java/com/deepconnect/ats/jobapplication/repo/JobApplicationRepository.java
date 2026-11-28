package com.deepconnect.ats.jobapplication.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import com.deepconnect.ats.jobapplication.interfaces.PositionCount;

import com.deepconnect.ats.jobapplication.model.JobApplication;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {

    @Override
    @EntityGraph(attributePaths = {"jobPosition", "jobPosition.tenant"})
    Page<JobApplication> findAll(Specification<JobApplication> specification, Pageable pageable);

    boolean existsByJobPositionIdAndEmail(Long jobPositionId, String email);

    boolean existsByRecruitmentPipelineStageAndClosedFalse(Long stageId);

    List<JobApplication> findByJobPositionPublicId(String jobPositionPublicId);

    List<JobApplication> findByJobPositionIdOrderByCreatedAtDesc(Long jobPositionId);

    Optional<JobApplication> findByPublicIdAndJobPositionId(String publicId, Long jobPositionId);

    @Modifying
    @Query("update JobApplication a set a.recruitmentPipelineStage = null "
            + "where a.closed = true and a.recruitmentPipelineStage in :stageIds")
    void clearClosedStages(@Param("stageIds") Collection<Long> stageIds);

    @Query("select a.jobPositionId as jobPositionId, count(a) as applicationCount from JobApplication a "
            + "where a.jobPositionId in :ids group by a.jobPositionId")
    List<PositionCount> countForPositions(@Param("ids") Collection<Long> ids);
}
