package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.ExamForward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamForwardRepository extends JpaRepository<ExamForward, Long> {
    List<ExamForward> findByCandidateResumeId(Long candidateResumeId);
    List<ExamForward> findByExamId(Long examId);
    List<ExamForward> findByToUserId(Long toUserId);
}
