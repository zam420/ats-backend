package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {
    List<Exam> findByCandidateResumeId(Long candidateResumeId);
    List<Exam> findByJobPositionId(Long jobPositionId);
}
