package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long>, JpaSpecificationExecutor<Candidate> {
    Optional<Candidate> findByPhoneNumber(String phoneNumber);
    Optional<Candidate> findByEmail(String email);
    List<Candidate> findByPhoneNumberOrEmail(String phoneNumber, String email);
}
