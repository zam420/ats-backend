package com.deepconnect.ats.candidatepipeline.repo;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationFeedback;

public interface CandidateEvaluationFeedbackRepository extends JpaRepository<CandidateEvaluationFeedback, Long> {
    Optional<CandidateEvaluationFeedback> findByCandidateEvaluationRoundIdAndInterviewerId(Long roundId, String interviewerId);

    List<CandidateEvaluationFeedback> findByCandidateEvaluationRoundId(Long roundId);

    List<CandidateEvaluationFeedback> findByCandidateEvaluationRoundIdInAndSubmittedTrue(Collection<Long> roundIds);
}
