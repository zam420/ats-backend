package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.InterviewRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRoundRepository extends JpaRepository<InterviewRound, Long> {
    List<InterviewRound> findByInterviewIdOrderByOrderAsc(Long interviewId);
}
