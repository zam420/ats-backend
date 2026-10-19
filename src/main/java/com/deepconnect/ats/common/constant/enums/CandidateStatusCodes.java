package com.deepconnect.ats.common.constant.enums;

/**
 * Django CandidateStatus values. The existing {@link CandidateStatus} enum does not match the Django values,
 * so the resume status schedule jobs use these codes.
 */
public final class CandidateStatusCodes {
    public static final int TALENT_POOL = -1;
    public static final int UN_SCREENED = 0;
    public static final int RESUME_SCREENING = 1;
    public static final int UNIDIRECTIONAL_VIDEO_INTERVIEW = 2;
    public static final int WRITTEN_EXAM = 3;
    public static final int HR_INTERVIEW = 4;
    public static final int PROFESSIONAL_INTERVIEW = 5;
    public static final int C_LEVEL_INTERVIEW = 6;
    public static final int OFFER_MANAGEMENT = 7;
    public static final int ONBOARDING_MANAGEMENT = 8;

    private CandidateStatusCodes() {
    }
}
