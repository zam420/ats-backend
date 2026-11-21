package com.deepconnect.ats.job.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum JobEmploymentType  implements LocalizedBaseEnum {

    FULL_TIME(0, "fullTime"),
    PART_TIME(1, "partTime"),
    CONTRACT(2, "contract"),
    INTERNSHIP(3, "internship"),
    FREELANCE(4, "freelance"),
    TEMPORARY(5, "temporary"),
    VOLUNTEER(6, "volunteer");

    @JsonValue
    private final Integer id;

    private final String key;

    private static final String enumsKeyPrefix = "enums.jobEmploymentType.";

    JobEmploymentType(Integer id, String key) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
    }

}