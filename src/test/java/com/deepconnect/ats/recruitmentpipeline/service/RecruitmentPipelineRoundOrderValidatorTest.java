package com.deepconnect.ats.recruitmentpipeline.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundBase;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundType;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RecruitmentPipelineRoundOrderValidatorTest {
    private static final List<RecruitmentPipelineRoundType> TYPES = List.of(
        RecruitmentPipelineRoundType.builder().id(1).isCustom(false).build(),
        RecruitmentPipelineRoundType.builder().id(2).isCustom(false).build(),
        RecruitmentPipelineRoundType.builder().id(3).isCustom(true).build()
    );

    static Stream<Supplier<? extends RecruitmentPipelineRoundBase>> roundTypes() {
        return Stream.of(RecruitmentPipelineRound::new, JobRecruitmentPipelineRound::new);
    }

    private RecruitmentPipelineRoundBase round(Supplier<? extends RecruitmentPipelineRoundBase> factory, int type, Integer order) {
        RecruitmentPipelineRoundBase round = factory.get();
        round.setRoundType(type);
        round.setOrder(order);
        return round;
    }

    @ParameterizedTest
    @MethodSource("roundTypes")
    void acceptsSortedCoreStagesAndRepeatedCustomStages(Supplier<? extends RecruitmentPipelineRoundBase> factory) {
        var rounds = List.of(round(factory, 2, null), round(factory, 3, 1),
            round(factory, 1, 0), round(factory, 3, 2));
        assertDoesNotThrow(() -> RecruitmentPipelineRoundHelper.assertRoundTypeOrder(TYPES, rounds, "user", "plan"));
    }

    @ParameterizedTest
    @MethodSource("roundTypes")
    void rejectsDuplicateOrOutOfOrderCoreStages(Supplier<? extends RecruitmentPipelineRoundBase> factory) {
        var duplicate = List.of(round(factory, 1, 0), round(factory, 1, 1));
        var reversed = List.of(round(factory, 2, 0), round(factory, 1, 1));
        assertThrows(InternalServerException.class,
            () -> RecruitmentPipelineRoundHelper.assertRoundTypeOrder(TYPES, duplicate, "user", "plan"));
        assertThrows(InternalServerException.class,
            () -> RecruitmentPipelineRoundHelper.assertRoundTypeOrder(TYPES, reversed, "user", "plan"));
    }
}
