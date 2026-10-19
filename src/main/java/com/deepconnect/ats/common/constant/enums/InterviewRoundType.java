package com.deepconnect.ats.common.constant.enums;

import lombok.Getter;

@Getter
public enum InterviewRoundType {
    HR_ROUND(2, "HR Interview", CandidateStatusCodes.HR_INTERVIEW),
    PROFESSIONAL_ROUND(3, "Professional Interview", CandidateStatusCodes.PROFESSIONAL_INTERVIEW),
    C_LEVEL_ROUND(4, "C-Level Interview", CandidateStatusCodes.C_LEVEL_INTERVIEW);

    private final int value;
    private final String displayName;
    private final int candidateStatus;

    InterviewRoundType(int value, String displayName, int candidateStatus) {
        this.value = value;
        this.displayName = displayName;
        this.candidateStatus = candidateStatus;
    }

    /** get_by_value: returns null when unknown. */
    public static InterviewRoundType getByValue(Integer value) {
        if (value == null) return null;
        for (InterviewRoundType t : values()) {
            if (t.value == value) return t;
        }
        return null;
    }

    /** mapper_to_candidate_status (Django CandidateStatus value). */
    public int mapperToCandidateStatus() {
        return candidateStatus;
    }
}
