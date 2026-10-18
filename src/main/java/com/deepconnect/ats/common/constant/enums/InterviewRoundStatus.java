package com.deepconnect.ats.common.constant.enums;

/** Django InterviewRounds.InterviewRoundStatus values. */
public final class InterviewRoundStatus {
    public static final int FINISHED = -2;
    public static final int CANCELLED = -1;
    public static final int DRAFT = 0;
    public static final int SCHEDULED = 1;
    public static final int IN_PROGRESS = 2;

    private InterviewRoundStatus() {
    }
}
