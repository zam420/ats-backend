package com.deepconnect.ats.common.constant.enums;

import lombok.Getter;

@Getter
public enum InterviewRoundType {
    WRITTEN_EXAM_ROUND(0, "Online Written Test", CandidateStatusCodes.WRITTEN_EXAM),
    UNIDIRECTIONAL_VIDEO_ROUND(1, "Video Interview", CandidateStatusCodes.UNIDIRECTIONAL_VIDEO_INTERVIEW),
    HR_ROUND(2, "HR Interview", CandidateStatusCodes.HR_INTERVIEW),
    PROFESSIONAL_ROUND(3, "Professional Interview", CandidateStatusCodes.PROFESSIONAL_INTERVIEW),
    CEO_ROUND(4, "CEO Interview", CandidateStatusCodes.CEO_INTERVIEW);

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
