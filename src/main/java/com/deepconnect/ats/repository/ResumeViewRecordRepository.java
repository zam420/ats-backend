package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.ResumeViewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeViewRecordRepository extends JpaRepository<ResumeViewRecord, Long> {
    Optional<ResumeViewRecord> findByCandidateResumeIdAndViewBy(Long candidateResumeId, Long viewBy);
    List<ResumeViewRecord> findByCandidateResumeId(Long candidateResumeId);
}
