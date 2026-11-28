package com.deepconnect.ats.candidatepipeline.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CandidateEvaluationStatus implements LocalizedBaseEnum {
    PENDING(0, "pending"),
    SCHEDULED(1, "scheduled"),
    AWAITING_FEEDBACK(3, "awaitingFeedback"),
    CONFLICTING_FEEDBACK(4, "conflictingFeedback"),
    COMPLETED(5, "completed");

    @JsonValue
    private final Integer id;
    private final String key;

    CandidateEvaluationStatus(Integer id, String key) {
        this.id = id;
        this.key = "enums.candidateEvaluationStatus." + key;
    }

    @JsonCreator
    public static CandidateEvaluationStatus fromId(Integer id) {
        for (CandidateEvaluationStatus status : values()) {
            if (status.id.equals(id)) return status;
        }
        throw new IllegalArgumentException("Invalid candidate evaluation status: " + id);
    }
}
