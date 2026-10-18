package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.CandidateWorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateWorkExperienceRepository extends JpaRepository<CandidateWorkExperience, Long> {
    List<CandidateWorkExperience> findByCandidateResumeId(Long candidateResumeId);
    List<CandidateWorkExperience> findByCandidateId(Long candidateId);
    void deleteByCandidateResumeId(Long candidateResumeId);
}
