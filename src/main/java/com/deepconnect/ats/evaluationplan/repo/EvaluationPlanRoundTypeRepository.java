package com.deepconnect.ats.evaluationplan.repo;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface EvaluationPlanRoundTypeRepository extends JpaRepository<EvaluationPlanRoundType, Integer> {
}
