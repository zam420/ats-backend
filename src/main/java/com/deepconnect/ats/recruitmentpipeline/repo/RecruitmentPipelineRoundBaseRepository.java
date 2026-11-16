package com.deepconnect.ats.recruitmentpipeline.repo;
import org.springframework.data.repository.NoRepositoryBean;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundBase;
import com.deepconnect.ats.common.repo.BaseRepository;

@NoRepositoryBean
public interface RecruitmentPipelineRoundBaseRepository<T extends RecruitmentPipelineRoundBase> extends BaseRepository<T, Long> {

}
