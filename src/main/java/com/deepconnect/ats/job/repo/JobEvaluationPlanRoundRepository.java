package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.evaluationplan.repo.EvaluationPlanRoundBaseRepository;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobEvaluationPlanRoundRepository extends EvaluationPlanRoundBaseRepository<JobEvaluationPlanRound> {
    
    List<JobEvaluationPlanRound> findByJobPositionIdAndActiveTrueOrderByOrderAsc(Long jobPositionId);

    List<JobEvaluationPlanRound> findByJobPositionIdAndPlanVersionOrderByOrderAsc(Long jobPositionId, Integer planVersion);

    @Query("select coalesce(max(r.planVersion), 0) from JobEvaluationPlanRound r where r.jobPositionId = ?1")
    int findLatestPlanVersion(Long jobPositionId);

    @Modifying
    @Query(value = "update job_evaluation_plan_round set active = false where job_position=?1 and active = true", nativeQuery = true)
    void archiveByJobPositionId(Long jobPositionId);

}
