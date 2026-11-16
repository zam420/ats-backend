package com.deepconnect.ats.screeningquestionsform.repo;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionBase;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ScreeningQuestionBaseRepository<T extends ScreeningQuestionBase> extends BaseRepository<T, Long> {
    
}
