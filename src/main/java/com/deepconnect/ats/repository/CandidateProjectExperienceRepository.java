package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.CandidateProjectExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateProjectExperienceRepository extends JpaRepository<CandidateProjectExperience, Long> {
    List<CandidateProjectExperience> findByCandidateResumeId(Long candidateResumeId);
    List<CandidateProjectExperience> findByCandidateId(Long candidateId);
    void deleteByCandidateResumeId(Long candidateResumeId);
}
