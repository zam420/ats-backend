package com.deepconnect.ats.screeningquestionsform.repo;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsFormShare;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreeningQuestionsFormShareRepository extends BaseRepository<ScreeningQuestionsFormShare, Long> {
    
    List<ScreeningQuestionsFormShare> findByScreeningQuestionsFormIdOrderByIdAsc(Long screeningQuestionTemplateId);

    @Modifying
    @Query(value = "delete from screening_question_template_share where screening_question_template=?1", nativeQuery = true)
    void deleteByScreeningQuestionsFormId(Long screeningQuestionTemplateId);
}
