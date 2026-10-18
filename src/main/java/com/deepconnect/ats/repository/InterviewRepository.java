package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long>, JpaSpecificationExecutor<Interview> {
    List<Interview> findByTargetJobId(Long targetJobId);
    List<Interview> findByInterviewStatus(Integer interviewStatus);
}
