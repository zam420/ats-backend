package com.deepconnect.ats.screeningquestionsform.repo;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreeningQuestionRepository extends ScreeningQuestionBaseRepository<ScreeningQuestion> {
    
    List<ScreeningQuestion> findByScreeningQuestionsFormIdOrderByIdAsc(Long screeningQuestionsFormId);

    @Modifying
    @Query(value = "delete from screening_question where screening_questions_form=?1", nativeQuery = true)
    void deleteByScreeningQuestionsFormId(Long screeningQuestionsFormId);

}
