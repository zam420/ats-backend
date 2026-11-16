package com.deepconnect.ats.recruitmentpipeline.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRound;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundBase;
import com.deepconnect.ats.evaluationplan.repo.EvaluationPlanRoundRepository;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundBase;
import jakarta.persistence.MappedSuperclass;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

class RoundRepositoryMappingTest {
    @Test
    void repositoriesTargetConcreteEntities() {
        assertEquals(RecruitmentPipelineRound.class,
                new DefaultRepositoryMetadata(RecruitmentPipelineRoundRepository.class).getDomainType());
        assertEquals(EvaluationPlanRound.class,
                new DefaultRepositoryMetadata(EvaluationPlanRoundRepository.class).getDomainType());
    }

    @Test
    void baseFieldsAreMappedForBothRoundEntities() {
        assertTrue(RecruitmentPipelineRoundBase.class.isAnnotationPresent(MappedSuperclass.class));
        assertTrue(EvaluationPlanRoundBase.class.isAnnotationPresent(MappedSuperclass.class));
    }
}
