package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.ResumeOperationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeOperationRecordRepository extends JpaRepository<ResumeOperationRecord, Long> {
    List<ResumeOperationRecord> findByCandidateResumeIdOrderByOperateAtDesc(Long candidateResumeId);
}
