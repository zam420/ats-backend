package com.deepconnect.ats.evaluationplan.repo;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundBase;
import com.deepconnect.ats.common.repo.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EvaluationPlanRoundBaseRepository<T extends EvaluationPlanRoundBase> extends BaseRepository<T, Long> {

}
