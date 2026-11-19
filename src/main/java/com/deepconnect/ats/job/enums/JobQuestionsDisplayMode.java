package com.deepconnect.ats.job.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum JobQuestionsDisplayMode  implements LocalizedBaseEnum {

    WITH_DESCRIPTION(0, "withDescription"),
    SEPARATE_TABS(1, "separateTabs");

    @JsonValue
    private final Integer id;

    private final String key;

    private static final String enumsKeyPrefix = "enums.jobQuestionsDisplayMode.";

    JobQuestionsDisplayMode(Integer id, String key) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
    }

    
}