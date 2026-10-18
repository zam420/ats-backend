package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.CandidateResumeForward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateResumeForwardRepository extends JpaRepository<CandidateResumeForward, Long> {
    List<CandidateResumeForward> findByCandidateResumeId(Long candidateResumeId);
    List<CandidateResumeForward> findByToUserId(Long toUserId);
    List<CandidateResumeForward> findByFromUserId(Long fromUserId);
}
