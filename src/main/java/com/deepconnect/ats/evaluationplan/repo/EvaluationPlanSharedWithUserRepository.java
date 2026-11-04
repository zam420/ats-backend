package com.deepconnect.ats.evaluationplan.repo;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlanSharedWithUser;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationPlanSharedWithUserRepository extends BaseRepository<EvaluationPlanSharedWithUser, Long> {
    
    List<EvaluationPlanSharedWithUser> findByEvaluationPlanIdOrderByIdAsc(Long evaluationPlanId);

    @Modifying
    @Query(value = "delete from evaluation_plan_shared_with_user where evaluation_plan=?1", nativeQuery = true)
    void deleteByEvaluationPlanId(Long evaluationPlanId);
}
