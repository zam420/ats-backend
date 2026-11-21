package com.deepconnect.ats.recruitmentpipeline.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundBase;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RecruitmentPipelineRoundOrderValidatorTest {

    static Stream<Supplier<? extends RecruitmentPipelineRoundBase>> roundTypes() {
        return Stream.of(RecruitmentPipelineRound::new, JobRecruitmentPipelineRound::new);
    }

    private RecruitmentPipelineRoundBase round(Supplier<? extends RecruitmentPipelineRoundBase> factory, RecruitmentPipelineRoundType type, Integer order) {
        RecruitmentPipelineRoundBase round = factory.get();
        round.setRoundType(type);
        round.setOrder(order);
        return round;
    }

    @ParameterizedTest
    @MethodSource("roundTypes")
    void acceptsSortedCoreStagesAndRepeatedCustomStages(Supplier<? extends RecruitmentPipelineRoundBase> factory) {
        var rounds = List.of(round(factory, RecruitmentPipelineRoundType.EVALUATING, null), round(factory, RecruitmentPipelineRoundType.CUSTOM, 1),
            round(factory, RecruitmentPipelineRoundType.SCREENING, 0), round(factory, RecruitmentPipelineRoundType.CUSTOM, 2));
        assertDoesNotThrow(() -> RecruitmentPipelineRoundHelper.assertRoundTypeOrder(rounds, "user", "plan"));
    }

    @ParameterizedTest
    @MethodSource("roundTypes")
    void rejectsDuplicateOrOutOfOrderCoreStages(Supplier<? extends RecruitmentPipelineRoundBase> factory) {
        var duplicate = List.of(round(factory, RecruitmentPipelineRoundType.SCREENING, 0), round(factory, RecruitmentPipelineRoundType.SCREENING, 1));
        var reversed = List.of(round(factory, RecruitmentPipelineRoundType.EVALUATING, 0), round(factory, RecruitmentPipelineRoundType.SCREENING, 1));
        assertThrows(InternalServerException.class,
            () -> RecruitmentPipelineRoundHelper.assertRoundTypeOrder(duplicate, "user", "plan"));
        assertThrows(InternalServerException.class,
            () -> RecruitmentPipelineRoundHelper.assertRoundTypeOrder(reversed, "user", "plan"));
    }
}
