package com.deepconnect.ats.screeningquestiontemplate.repo;

import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestion;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreeningQuestionRepository extends BaseRepository<ScreeningQuestion, Long> {
    
    List<ScreeningQuestion> findByScreeningQuestionTemplateIdOrderByIdAsc(Long screeningQuestionTemplateId);

    @Modifying
    @Query(value = "delete from screening_question where screening_question_template=?1", nativeQuery = true)
    void deleteByScreeningQuestionTemplateId(Long screeningQuestionId);
}
