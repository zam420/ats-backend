package com.deepconnect.ats.candidatepipeline.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CandidateEvaluationStatus implements LocalizedBaseEnum {
    PENDING(0, "pending"),
    SCHEDULED(1, "scheduled"),
    IN_PROGRESS(2, "inProgress"),
    AWAITING_FEEDBACK(3, "awaitingFeedback"),
    COMPLETED(4, "completed");

    @JsonValue
    private final Integer id;
    private final String key;

    CandidateEvaluationStatus(Integer id, String key) {
        this.id = id;
        this.key = "enums.candidateEvaluationStatus." + key;
    }
}
