package com.deepconnect.ats.interviewplan.repo;

import com.deepconnect.ats.interviewplan.model.InterviewPlanRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewProcessRoundRepository extends JpaRepository<InterviewPlanRound, Long> {
    List<InterviewPlanRound> findByInterviewPlanIdOrderByOrderAsc(Long interviewPlanId);

    @Modifying
    @Query(value = "delete from interview_plan_round where interview_plan=?1", nativeQuery = true)
    void deleteByInterviewPlanId(Long interviewPlanId);
}
