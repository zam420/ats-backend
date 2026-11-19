package com.deepconnect.ats.job.enums;
import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum JobStatus implements LocalizedBaseEnum {

    DRAFT(0, "draft"),
    OPEN(1, "open"),
    CLOSED(2, "closed");

    @JsonValue
    private final Integer id;

    private final String key;

    private static final String enumsKeyPrefix = "enums.jobStatus.";

    JobStatus(Integer id, String key) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
    }

    
}
