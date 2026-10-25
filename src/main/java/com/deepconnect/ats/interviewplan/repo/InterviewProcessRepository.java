package com.deepconnect.ats.interviewplan.repo;

import com.deepconnect.ats.interviewplan.model.InterviewPlan;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface InterviewProcessRepository extends BaseRepository<InterviewPlan, Long> {
    List<InterviewPlan> findByStatus(Integer status);

    // Add Pageable parameter and return Page<InterviewProcess>
    Page<InterviewPlan> findByStatus(Integer status, Pageable pageable);
}
