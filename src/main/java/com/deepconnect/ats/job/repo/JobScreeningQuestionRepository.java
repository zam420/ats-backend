package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.screeningquestionsform.repo.ScreeningQuestionBaseRepository;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobScreeningQuestionRepository extends ScreeningQuestionBaseRepository<JobScreeningQuestion> {
    
    List<JobScreeningQuestion> findByJobPositionIdOrderByIdAsc(Long jobPositionId);

    @Query("select coalesce(max(q.questionSetVersion), 0) from JobScreeningQuestion q where q.jobPositionId = ?1")
    int findLatestQuestionSetVersion(Long jobPositionId);

    @Modifying
    @Query(value = "update job_screening_question set active = false where job_position=?1 and active = true", nativeQuery = true)
    void archiveByJobPositionId(Long jobPositionId);

}
