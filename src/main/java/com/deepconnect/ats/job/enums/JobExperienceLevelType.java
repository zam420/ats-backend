package com.deepconnect.ats.job.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum JobExperienceLevelType  implements LocalizedBaseEnum {

    JUNIOR(0, "junior"),
    MID_LEVEL(1, "midLevel"),
    SENIOR(2, "senior");

    @JsonValue
    private final Integer id;

    private final String key;

    private static final String enumsKeyPrefix = "enums.jobExperienceLevelType.";

    JobExperienceLevelType(Integer id, String key) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
    }

}