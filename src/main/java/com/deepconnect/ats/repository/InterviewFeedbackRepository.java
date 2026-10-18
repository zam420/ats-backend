package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedback, Long> {
    Optional<InterviewFeedback> findByInterviewCandidateId(Long interviewCandidateId);
    List<InterviewFeedback> findByInterviewRoundId(Long interviewRoundId);
}
