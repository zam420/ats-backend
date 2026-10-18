package com.deepconnect.ats.service.resume;

import com.deepconnect.ats.common.constant.enums.CandidateStatusCodes;
import com.deepconnect.ats.common.constant.enums.CandidateSubStatus;
import com.deepconnect.ats.common.constant.enums.InterviewResult;
import com.deepconnect.ats.common.constant.enums.InterviewRoundType;
import com.deepconnect.ats.common.constant.enums.ResumeOperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Port of Django ResumeOperation dto. operationArgs semantics (same as Python):
 * MOVE_TO -> Map (move_to_status = Integer CandidateStatus value, ...);
 * SCHEDULED_INTERVIEW / INTERVIEW_END_TIME_REACHED -> InterviewRoundType;
 * INTERVIEW_FEEDBACK -> Object[]{InterviewRoundType, InterviewResult[, lastOperator]}.
 */
@Getter
@Setter
@AllArgsConstructor
public class ResumeOperation {

    private ResumeOperationType operationType;
    private Object operationArgs;

    public ResumeOperation(ResumeOperationType operationType) {
        this(operationType, null);
    }

    public boolean isMoveToOperation() {
        return operationType == ResumeOperationType.MOVE_TO;
    }

    public boolean isInterviewOperation() {
        return operationType == ResumeOperationType.SCHEDULED_INTERVIEW
                || operationType == ResumeOperationType.INTERVIEW_END_TIME_REACHED
                || operationType == ResumeOperationType.INTERVIEW_FEEDBACK;
    }

    /** Django CandidateStatus value, or null. */
    public Integer moveToStatus() {
        if (operationArgs instanceof Map<?, ?> m) {
            Object v = m.get("move_to_status");
            return v instanceof Integer i ? i : null;
        }
        return null;
    }

    public static ResumeOperation moveToOperation(int moveToStatus, Map<String, Object> kwargs) {
        Map<String, Object> args = new HashMap<>();
        if (kwargs != null) {
            args.putAll(kwargs);
        }
        args.put("move_to_status", moveToStatus);
        return new ResumeOperation(ResumeOperationType.MOVE_TO, args);
    }

    public static ResumeOperation moveToWrittenExam(CandidateSubStatus subStatus) {
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("sub_status", subStatus);
        return moveToOperation(CandidateStatusCodes.WRITTEN_EXAM, kwargs);
    }

    public static ResumeOperation scheduledInterviewOperation(InterviewRoundType roundType) {
        return new ResumeOperation(ResumeOperationType.SCHEDULED_INTERVIEW, roundType);
    }

    public static ResumeOperation interviewEndTimeReachedOperation(InterviewRoundType roundType) {
        return new ResumeOperation(ResumeOperationType.INTERVIEW_END_TIME_REACHED, roundType);
    }

    public static ResumeOperation interviewFeedbackOperation(InterviewRoundType roundType, InterviewResult result) {
        return new ResumeOperation(ResumeOperationType.INTERVIEW_FEEDBACK, new Object[]{roundType, result});
    }

    public static ResumeOperation forwardOperation(Object forwardTo) {
        return new ResumeOperation(ResumeOperationType.FORWARD, forwardTo);
    }

    public static ResumeOperation forwardFeedbackOperation(Object feedbackResult) {
        return new ResumeOperation(ResumeOperationType.FORWARD_FEEDBACK, feedbackResult);
    }

    public static ResumeOperation offerSentOperation() {
        return new ResumeOperation(ResumeOperationType.SENT_OFFER);
    }

    public static ResumeOperation onboardingTimeReached() {
        return new ResumeOperation(ResumeOperationType.ONBOARDING_TIME_REACHED);
    }

    public static ResumeOperation interviewFeedbackUpdateOperation(InterviewRoundType roundType,
                                                                   InterviewResult result, Object lastOperator) {
        return new ResumeOperation(ResumeOperationType.INTERVIEW_FEEDBACK,
                new Object[]{roundType, result, lastOperator});
    }
}
