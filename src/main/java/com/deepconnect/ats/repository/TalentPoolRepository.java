package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.TalentPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TalentPoolRepository extends JpaRepository<TalentPool, Long>, JpaSpecificationExecutor<TalentPool> {
    Optional<TalentPool> findByCandidateResumeId(Long candidateResumeId);
    void deleteByCandidateResumeId(Long candidateResumeId);
}
