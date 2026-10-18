package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.ResumeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeCommentRepository extends JpaRepository<ResumeComment, Long> {
    List<ResumeComment> findByCandidateResumeIdOrderByCommentAtDesc(Long candidateResumeId);
}
