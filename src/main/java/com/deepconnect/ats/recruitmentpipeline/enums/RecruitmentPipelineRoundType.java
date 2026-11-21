package com.deepconnect.ats.recruitmentpipeline.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseExtendedEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum RecruitmentPipelineRoundType  implements LocalizedBaseExtendedEnum {

    APPLIED(0, "applied"),
    SCREENING(1, "screening"),
    EVALUATING(2, "evaluating"),
    OFFERED(3, "offered"),
    HIRED(4, "hired"),
    ONBOARDED(5, "onboarded"),
    OFFER_APPROVAL(6, "offerApproval", true),
    BACKGROUND_CHECK(7, "backgroundCheck", true),
    CUSTOM(8, "custom", true);

    @JsonValue
    private final Integer id;

    private final String key;
    private final String code;
    private final Boolean isCustom;

    private static final String enumsKeyPrefix = "enums.recruitmentPipelineRoundType.";

     RecruitmentPipelineRoundType(Integer id, String key) {
        this(id, key, false);
    }

    RecruitmentPipelineRoundType(Integer id, String key, Boolean isCustom) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
        this.code = isCustom ? "CUSTOM" : this.name();
        this.isCustom = isCustom;
    }

    
}