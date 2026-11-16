package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.evaluationplan.repo.EvaluationPlanRoundBaseRepository;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobEvaluationPlanRoundRepository extends EvaluationPlanRoundBaseRepository<JobEvaluationPlanRound> {
    
    List<JobEvaluationPlanRound> findByJobPositionIdOrderByOrderAsc(Long jobPositionId);

    @Modifying
    @Query(value = "delete from job_evaluation_plan_round where job_position=?1", nativeQuery = true)
    void deleteByJobPositionId(Long jobPositionId);

}
