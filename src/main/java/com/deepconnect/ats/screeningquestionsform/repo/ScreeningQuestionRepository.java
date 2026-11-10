package com.deepconnect.ats.screeningquestionsform.repo;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreeningQuestionRepository extends BaseRepository<ScreeningQuestion, Long> {
    
    List<ScreeningQuestion> findByScreeningQuestionsFormIdOrderByIdAsc(Long screeningQuestionsFormId);

    @Modifying
    @Query(value = "delete from screening_question where screening_questions_form=?1", nativeQuery = true)
    void deleteByScreeningQuestionsFormId(Long screeningQuestionsFormId);
}
