package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.CandidateResumeOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateResumeOfferRepository extends JpaRepository<CandidateResumeOffer, Long> {
    Optional<CandidateResumeOffer> findByCandidateResumeId(Long candidateResumeId);
}
