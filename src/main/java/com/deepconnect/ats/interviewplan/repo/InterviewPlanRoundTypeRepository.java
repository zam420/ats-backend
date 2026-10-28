package com.deepconnect.ats.interviewplan.repo;

import com.deepconnect.ats.interviewplan.model.InterviewPlanRoundType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface InterviewPlanRoundTypeRepository extends JpaRepository<InterviewPlanRoundType, Integer> {
}
