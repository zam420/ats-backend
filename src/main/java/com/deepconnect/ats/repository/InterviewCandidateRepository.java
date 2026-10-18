package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.InterviewCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewCandidateRepository extends JpaRepository<InterviewCandidate, Long> {
    List<InterviewCandidate> findByRoundId(Long roundId);
    List<InterviewCandidate> findByCandidateResumeId(Long candidateResumeId);
    Optional<InterviewCandidate> findByRoundIdAndCandidateResumeId(Long roundId, Long candidateResumeId);
}
