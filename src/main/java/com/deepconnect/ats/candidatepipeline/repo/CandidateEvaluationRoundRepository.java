package com.deepconnect.ats.candidatepipeline.repo;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;

public interface CandidateEvaluationRoundRepository extends JpaRepository<CandidateEvaluationRound, Long> {
    List<CandidateEvaluationRound> findByJobApplicationIdOrderByOrderAsc(Long jobApplicationId);
    List<CandidateEvaluationRound> findByJobApplicationIdInOrderByOrderAsc(Collection<Long> jobApplicationIds);

    Optional<CandidateEvaluationRound> findByPublicIdAndJobApplicationId(String publicId, Long jobApplicationId);
}
