package com.deepconnect.ats.interviewprocess.repo;

import com.deepconnect.ats.interviewprocess.model.InterviewProcessRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewProcessRoundRepository extends JpaRepository<InterviewProcessRound, Long> {
    List<InterviewProcessRound> findByIptIdOrderByOrderAsc(Long iptId);
}
