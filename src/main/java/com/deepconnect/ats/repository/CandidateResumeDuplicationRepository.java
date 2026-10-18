package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.CandidateResumeDuplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateResumeDuplicationRepository extends JpaRepository<CandidateResumeDuplication, Long> {
    List<CandidateResumeDuplication> findByCandidateResumeId(Long candidateResumeId);
}
