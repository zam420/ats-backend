package com.deepconnect.ats.evaluationplan.repo;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRound;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationPlanRoundRepository extends BaseRepository<EvaluationPlanRound, Long> {
    
    List<EvaluationPlanRound> findByEvaluationPlanIdOrderByOrderAsc(Long evaluationPlanId);

    @Modifying
    @Query(value = "delete from evaluation_plan_round where evaluation_plan=?1", nativeQuery = true)
    void deleteByEvaluationPlanId(Long evaluationPlanId);
}
