package com.deepconnect.ats.common.enums;

import lombok.Getter;

@Getter
public enum ValidationFieldType {
    RECRUITMENT_PIPELINE_STAGE("recruitmentPipelineRound.fieldDescription"),
    EVALUATION_PLAN_ROUND("evaluationPlanRound.fieldDescription"),
    ;

    private final String key;

    ValidationFieldType(String key) {
        this.key = key;
    }
}
