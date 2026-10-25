package com.deepconnect.ats.common.constant.enums;

import lombok.Getter;

import java.time.LocalDate;

import com.deepconnect.ats.common.utils.TimeUtil;

@Getter
public enum CandidateSubStatus {
    TO_FORWARD(CandidateStatusCodes.RESUME_SCREENING, -1),
    TO_BE_REVIEW(CandidateStatusCodes.RESUME_SCREENING, 0),
    REVIEWED(CandidateStatusCodes.RESUME_SCREENING, 10),

    HR_ITV_PENDING(CandidateStatusCodes.HR_INTERVIEW, 40),
    HR_ITV_SCHEDULED(CandidateStatusCodes.HR_INTERVIEW, 41),
    HR_ITV_FEEDBACK_PENDING(CandidateStatusCodes.HR_INTERVIEW, 42),
    HR_ITV_FEEDBACK_RECEIVED(CandidateStatusCodes.HR_INTERVIEW, 43),

    TECH_ITV_PENDING(CandidateStatusCodes.FUNCTIONAL_INTERVIEW, 50),
    TECH_ITV_SCHEDULED(CandidateStatusCodes.FUNCTIONAL_INTERVIEW, 51),
    TECH_ITV_FEEDBACK_PENDING(CandidateStatusCodes.FUNCTIONAL_INTERVIEW, 52),
    TECH_ITV_FEEDBACK_RECEIVED(CandidateStatusCodes.FUNCTIONAL_INTERVIEW, 53),

    CEO_ITV_PENDING(CandidateStatusCodes.EXECUTIVE_INTERVIEW, 60),
    CEO_ITV_SCHEDULED(CandidateStatusCodes.EXECUTIVE_INTERVIEW, 61),
    CEO_ITV_FEEDBACK_PENDING(CandidateStatusCodes.EXECUTIVE_INTERVIEW, 62),
    CEO_ITV_FEEDBACK_RECEIVED(CandidateStatusCodes.EXECUTIVE_INTERVIEW, 63),

    OFFER_PENDING(CandidateStatusCodes.OFFER_MANAGEMENT, 70),
    OFFER_SENT(CandidateStatusCodes.OFFER_MANAGEMENT, 71),

    ONBOARDING_ONGOING(CandidateStatusCodes.ONBOARDING_MANAGEMENT, 80),
    ONBOARDED(CandidateStatusCodes.ONBOARDING_MANAGEMENT, 81),

    WRITTEN_EXAM_PENDING(CandidateStatusCodes.WRITTEN_EXAM, 90),
    WRITTEN_EXAM_SCHEDULED(CandidateStatusCodes.WRITTEN_EXAM, 91),
    WRITTEN_EXAM_COMPLETED(CandidateStatusCodes.WRITTEN_EXAM, 92);

    private final int candidateStatus;
    private final int code;

    CandidateSubStatus(int candidateStatus, int code) {
        this.candidateStatus = candidateStatus;
        this.code = code;
    }

    public static CandidateSubStatus getByCode(Integer code) {
        if (code == null) return null;
        for (CandidateSubStatus s : values()) {
            if (s.code == code) return s;
        }
        return null;
    }

    public static CandidateSubStatus getOnboardingSubStatusByDate(LocalDate expectOnboardingDate) {
        if (!expectOnboardingDate.isAfter(TimeUtil.now().toLocalDate())) {
            return ONBOARDED;
        }
        return ONBOARDING_ONGOING;
    }
}
