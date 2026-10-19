package com.deepconnect.ats.interviewprocess.repo;

import com.deepconnect.ats.interviewprocess.model.InterviewProcessRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewProcessRoundRepository extends JpaRepository<InterviewProcessRound, Long> {
    List<InterviewProcessRound> findByIptIdOrderByOrderAsc(Long iptId);

    @Modifying
    @Query(value = "delete from interview_process_round where interview_process=?1", nativeQuery = true)
    void deleteByInterviewProcessId(Long interviewProcessId);
}
