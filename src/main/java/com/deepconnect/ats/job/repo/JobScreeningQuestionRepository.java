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

    @Modifying
    @Query(value = "delete from job_screening_question where job_position=?1", nativeQuery = true)
    void deleteByJobPositionId(Long jobPositionId);

}
