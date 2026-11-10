package com.deepconnect.ats.screeningquestiontemplate.repo;

import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestionTemplateShare;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreeningQuestionTemplateShareRepository extends BaseRepository<ScreeningQuestionTemplateShare, Long> {
    
    List<ScreeningQuestionTemplateShare> findByScreeningQuestionTemplateIdOrderByIdAsc(Long screeningQuestionTemplateId);

    @Modifying
    @Query(value = "delete from screening_question_template_share where screening_question_template=?1", nativeQuery = true)
    void deleteByScreeningQuestionTemplateId(Long screeningQuestionTemplateId);
}
