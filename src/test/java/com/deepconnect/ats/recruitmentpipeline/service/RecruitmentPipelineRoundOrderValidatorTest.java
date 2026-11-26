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
        var rounds = List.of(
            round(factory, RecruitmentPipelineRoundType.APPLIED, 0),
            round(factory, RecruitmentPipelineRoundType.CUSTOM, 1),
            round(factory, RecruitmentPipelineRoundType.SCREENING, 2),
            round(factory, RecruitmentPipelineRoundType.CUSTOM, 3),
            round(factory, RecruitmentPipelineRoundType.EVALUATING, 4),
            round(factory, RecruitmentPipelineRoundType.OFFERED, 5),
            round(factory, RecruitmentPipelineRoundType.HIRED, 6),
            round(factory, RecruitmentPipelineRoundType.ONBOARDED, 7));
        assertDoesNotThrow(() -> RecruitmentPipelineRoundHelper.assertRoundTypeOrder(rounds, "user", "plan"));
    }

    @ParameterizedTest
    @MethodSource("roundTypes")
    void rejectsDuplicateOrOutOfOrderCoreStages(Supplier<? extends RecruitmentPipelineRoundBase> factory) {
        var duplicate = List.of(
            round(factory, RecruitmentPipelineRoundType.APPLIED, 0),
            round(factory, RecruitmentPipelineRoundType.SCREENING, 1),
            round(factory, RecruitmentPipelineRoundType.SCREENING, 2));
        var reversed = List.of(
            round(factory, RecruitmentPipelineRoundType.APPLIED, 0),
            round(factory, RecruitmentPipelineRoundType.EVALUATING, 1),
            round(factory, RecruitmentPipelineRoundType.SCREENING, 2));
        assertThrows(InternalServerException.class,
            () -> RecruitmentPipelineRoundHelper.assertRoundTypeOrder(duplicate, "user", "plan"));
        assertThrows(InternalServerException.class,
            () -> RecruitmentPipelineRoundHelper.assertRoundTypeOrder(reversed, "user", "plan"));
    }

    @ParameterizedTest
    @MethodSource("roundTypes")
    void requiresOfferApprovalBeforeOffered(Supplier<? extends RecruitmentPipelineRoundBase> factory) {
        var offerApprovalBeforeOffered = List.of(
            round(factory, RecruitmentPipelineRoundType.APPLIED, 0),
            round(factory, RecruitmentPipelineRoundType.OFFERED, 1),
            round(factory, RecruitmentPipelineRoundType.OFFER_APPROVAL, 2));
        var offerApprovalAfterOffered = List.of(
            round(factory, RecruitmentPipelineRoundType.APPLIED, 0),
            round(factory, RecruitmentPipelineRoundType.OFFER_APPROVAL, 1),
            round(factory, RecruitmentPipelineRoundType.OFFERED, 2));

        assertThrows(InternalServerException.class, () ->
            RecruitmentPipelineRoundHelper.assertRoundTypeOrder(offerApprovalBeforeOffered, "user", "plan"));
        assertDoesNotThrow(() ->
            RecruitmentPipelineRoundHelper.assertRoundTypeOrder(offerApprovalAfterOffered, "user", "plan"));
    }
}
