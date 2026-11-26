package com.deepconnect.ats.candidatepipeline.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CandidateEvaluationResult implements LocalizedBaseEnum {
    STRONG_YES(0, "strongYes"),
    STRONG_NO(1, "strongNo"),
    CANCELLED(2, "cancelled"),
    YES(3, "yes"),
    NO(4, "no");

    @JsonValue
    private final Integer id;
    private final String key;

    CandidateEvaluationResult(Integer id, String key) {
        this.id = id;
        this.key = "enums.candidateEvaluationResult." + key;
    }
}
