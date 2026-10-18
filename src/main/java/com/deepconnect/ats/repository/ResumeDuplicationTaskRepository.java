package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.ResumeDuplicationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeDuplicationTaskRepository extends JpaRepository<ResumeDuplicationTask, Long> {
    List<ResumeDuplicationTask> findByResumeId(Integer resumeId);
}
