package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.ResumeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeTagRepository extends JpaRepository<ResumeTag, Long> {
    List<ResumeTag> findByCandidateResumeId(Long candidateResumeId);
    Optional<ResumeTag> findByCandidateResumeIdAndTagId(Long candidateResumeId, Long tagId);
    void deleteByCandidateResumeId(Long candidateResumeId);
}
