package com.deepconnect.ats.interviewprocess.repo;

import com.deepconnect.ats.interviewprocess.model.InterviewProcess;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface InterviewProcessRepository extends BaseRepository<InterviewProcess, Long> {
    List<InterviewProcess> findByStatus(Integer status);

    // Add Pageable parameter and return Page<InterviewProcess>
    Page<InterviewProcess> findByStatus(Integer status, Pageable pageable);
}
