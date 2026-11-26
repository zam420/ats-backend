package com.deepconnect.ats.candidatepipeline.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CandidateEvaluationResult implements LocalizedBaseEnum {
    PASSED(0, "passed"),
    FAILED(1, "failed"),
    CANCELLED(2, "cancelled");

    @JsonValue
    private final Integer id;
    private final String key;

    CandidateEvaluationResult(Integer id, String key) {
        this.id = id;
        this.key = "enums.candidateEvaluationResult." + key;
    }
}
