package com.deepconnect.ats.job.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum JobWorkArrangementType  implements LocalizedBaseEnum {

    ONSITE(0, "onsite"),
    REMOTE(1, "remote"),
    HYBRID(2, "hybrid");

    @JsonValue
    private final Integer id;

    private final String key;

    private static final String enumsKeyPrefix = "enums.jobWorkArrangementType.";

    JobWorkArrangementType(Integer id, String key) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
    }

}