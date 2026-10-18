package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.CandidateEducationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateEducationHistoryRepository extends JpaRepository<CandidateEducationHistory, Long> {
    List<CandidateEducationHistory> findByCandidateResumeId(Long candidateResumeId);
    List<CandidateEducationHistory> findByCandidateId(Long candidateId);
    void deleteByCandidateResumeId(Long candidateResumeId);
}
