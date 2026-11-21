package com.deepconnect.ats.evaluationplan.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseExtendedEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum EvaluationPlanRoundType  implements LocalizedBaseExtendedEnum {

    HR(0, "hr"),
    FUNCTIONAL(1, "functional"),
    EXECUTIVE(2, "executive"),
    CUSTOM(3, "custom", true);

    @JsonValue
    private final Integer id;

    private final String key;
    private final String code;
    private final Boolean isCustom;

    private static final String enumsKeyPrefix = "enums.evaluationPlanRoundType.";

    EvaluationPlanRoundType(Integer id, String key) {
        this(id, key, false);
    }

    EvaluationPlanRoundType(Integer id, String key, Boolean isCustom) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
        this.code = isCustom ? "CUSTOM" : this.name();
        this.isCustom = isCustom;
    }

    
}