package com.deepconnect.ats.service.resume;

import com.deepconnect.ats.common.constant.enums.CandidateStatusCodes;
import com.deepconnect.ats.common.constant.enums.CandidateSubStatus;
import com.deepconnect.ats.common.constant.enums.InterviewRoundTypeEnum;
import com.deepconnect.ats.common.constant.enums.ResumeOperationType;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.model.Exam;
import com.deepconnect.ats.repository.ScheduleJobQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/** Port of Django candidate_status_calculator. */
@Component
@RequiredArgsConstructor
public class CandidateStatusCalculator {

    /** Django ExamStatus values. */
    private static final int EXAM_SCHEDULED = 0;
    private static final int EXAM_COMPLETED = 1;
    private static final int EXAM_SUBMITTED = 2;
    private static final int EXAM_SYNCED = 3;
    private static final int EXAM_DELETED = 4;
    private static final int EXAM_EVALUATED = 5;

    /** (CandidateStatus value, CandidateSubStatus or null). */
    public record StatusResult(int status, CandidateSubStatus subStatus) {
    }

    private final ScheduleJobQueryRepository queryRepository;

    @SuppressWarnings("unchecked")
    public StatusResult calculateStatusByOperation(ResumeOperation operation) {
        ResumeOperationType operationType = operation.getOperationType();
        if (operationType == ResumeOperationType.MOVE_TO) {
            Integer moveToStatus = operation.moveToStatus();
            if (moveToStatus == null) {
                throw new InternalServerException("move_to_status 不能为空");
            }
            return calculateForMoveTo(moveToStatus, (Map<String, Object>) operation.getOperationArgs());
        }
        if (operationType == ResumeOperationType.FORWARD) {
            return new StatusResult(CandidateStatusCodes.RESUME_SCREENING, CandidateSubStatus.TO_BE_REVIEW);
        }
        if (operationType == ResumeOperationType.FORWARD_FEEDBACK) {
            return new StatusResult(CandidateStatusCodes.RESUME_SCREENING, CandidateSubStatus.REVIEWED);
        }
        if (operation.isInterviewOperation()) {
            return calculateForInterview(operationType, operation.getOperationArgs());
        }
        if (operationType == ResumeOperationType.SENT_OFFER) {
            return new StatusResult(CandidateStatusCodes.OFFER_MANAGEMENT, CandidateSubStatus.OFFER_SENT);
        }
        if (operationType == ResumeOperationType.ONBOARDING_TIME_REACHED) {
            return new StatusResult(CandidateStatusCodes.ONBOARDING_MANAGEMENT, CandidateSubStatus.ONBOARDED);
        }
        if (operationType == ResumeOperationType.WRITTEN_TEST) {
            Map<String, Object> args = (Map<String, Object>) operation.getOperationArgs();
            Long resumeId = ((Number) args.get("resume_id")).longValue();
            return new StatusResult(CandidateStatusCodes.WRITTEN_EXAM, calcWrittenTestSubStatus(resumeId));
        }
        // Python returns None for unhandled operation types
        return null;
    }

    public StatusResult calculateForMoveTo(int moveToStatus, Map<String, Object> operateArgs) {
        CandidateSubStatus subStatus = null;
        switch (moveToStatus) {
            case CandidateStatusCodes.RESUME_SCREENING:
                subStatus = CandidateSubStatus.TO_FORWARD;
                break;
            case CandidateStatusCodes.WRITTEN_EXAM:
                subStatus = (CandidateSubStatus) operateArgs.get("sub_status");
                if (subStatus == null) {
                    Long resumeId = ((Number) operateArgs.get("resume_id")).longValue();
                    subStatus = calcWrittenTestSubStatus(resumeId);
                }
                break;
            case CandidateStatusCodes.HR_INTERVIEW:
                subStatus = CandidateSubStatus.HR_ITV_PENDING;
                break;
            case CandidateStatusCodes.FUNCTIONAL_INTERVIEW:
                subStatus = CandidateSubStatus.TECH_ITV_PENDING;
                break;
            case CandidateStatusCodes.EXECUTIVE_INTERVIEW:
                subStatus = CandidateSubStatus.CEO_ITV_PENDING;
                break;
            case CandidateStatusCodes.OFFER_MANAGEMENT:
                subStatus = CandidateSubStatus.OFFER_PENDING;
                break;
            case CandidateStatusCodes.ONBOARDING_MANAGEMENT:
                subStatus = CandidateSubStatus.getOnboardingSubStatusByDate(
                        (LocalDate) operateArgs.get("expect_onboarding_date"));
                break;
            default:
                break;
        }
        return new StatusResult(moveToStatus, subStatus);
    }

    /** operateArgs: InterviewRoundType, or Object[]{InterviewRoundType, ...} for INTERVIEW_FEEDBACK. */
    public StatusResult calculateForInterview(ResumeOperationType operation, Object operateArgs) {
        InterviewRoundTypeEnum interviewRound;
        if (operation == ResumeOperationType.INTERVIEW_FEEDBACK) {
            interviewRound = (InterviewRoundTypeEnum) ((Object[]) operateArgs)[0];
        } else {
            interviewRound = (InterviewRoundTypeEnum) operateArgs;
        }
        if (interviewRound == null) {
            throw new InternalServerException("interview_round 不能为空");
        }
        int candidateStatus = interviewRound.getValue();
        CandidateSubStatus subStatus = null;
        if (operation == ResumeOperationType.SCHEDULED_INTERVIEW) {
            subStatus = interviewScheduledStatus(interviewRound);
        } else if (operation == ResumeOperationType.INTERVIEW_END_TIME_REACHED) {
            subStatus = interviewFeedbackPendingStatus(interviewRound);
        } else if (operation == ResumeOperationType.INTERVIEW_FEEDBACK) {
            subStatus = interviewFeedbackStatus(interviewRound);
        }
        return new StatusResult(candidateStatus, subStatus);
    }

    private CandidateSubStatus interviewScheduledStatus(InterviewRoundTypeEnum round) {
        return switch (round) {
            case HR_ROUND -> CandidateSubStatus.HR_ITV_SCHEDULED;
            case FUNCTIONAL_ROUND -> CandidateSubStatus.TECH_ITV_SCHEDULED;
            case C_LEVEL_ROUND -> CandidateSubStatus.CEO_ITV_SCHEDULED;
            default -> null;
        };
    }

    private CandidateSubStatus interviewFeedbackPendingStatus(InterviewRoundTypeEnum round) {
        return switch (round) {
            case HR_ROUND -> CandidateSubStatus.HR_ITV_FEEDBACK_PENDING;
            case FUNCTIONAL_ROUND -> CandidateSubStatus.TECH_ITV_FEEDBACK_PENDING;
            case C_LEVEL_ROUND -> CandidateSubStatus.CEO_ITV_FEEDBACK_PENDING;
            default -> null;
        };
    }

    private CandidateSubStatus interviewFeedbackStatus(InterviewRoundTypeEnum round) {
        return switch (round) {
            case HR_ROUND -> CandidateSubStatus.HR_ITV_FEEDBACK_RECEIVED;
            case FUNCTIONAL_ROUND -> CandidateSubStatus.TECH_ITV_FEEDBACK_RECEIVED;
            case C_LEVEL_ROUND -> CandidateSubStatus.CEO_ITV_FEEDBACK_RECEIVED;
            default -> null;
        };
    }

    public CandidateSubStatus calcWrittenTestSubStatus(Long resumeId) {
        Exam exam = queryRepository.findLatestSubmittedExam(resumeId);
        if (exam != null && exam.getStatus() != null) {
            switch (exam.getStatus()) {
                case EXAM_SCHEDULED:
                    return CandidateSubStatus.WRITTEN_EXAM_SCHEDULED;
                case EXAM_COMPLETED:
                case EXAM_SUBMITTED:
                case EXAM_SYNCED:
                case EXAM_EVALUATED:
                    return CandidateSubStatus.WRITTEN_EXAM_COMPLETED;
                case EXAM_DELETED:
                    return CandidateSubStatus.WRITTEN_EXAM_PENDING;
                default:
                    break;
            }
        }
        return CandidateSubStatus.WRITTEN_EXAM_PENDING;
    }
}
