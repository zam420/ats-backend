package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.CandidateResume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateResumeRepository extends JpaRepository<CandidateResume, Long>, JpaSpecificationExecutor<CandidateResume> {
    List<CandidateResume> findByCandidateId(Long candidateId);
    List<CandidateResume> findByApplyingPositionId(Long applyingPositionId);
    List<CandidateResume> findByStatus(Integer status);
    List<CandidateResume> findByEmail(String email);
    List<CandidateResume> findByPhoneNumber(String phoneNumber);
    Page<CandidateResume> findByDataStatus(Integer dataStatus, Pageable pageable);
}
