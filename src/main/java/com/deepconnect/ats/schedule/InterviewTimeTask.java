package com.deepconnect.ats.schedule;

import com.deepconnect.ats.common.constant.enums.InterviewCandidateStatus;
import com.deepconnect.ats.common.constant.enums.InterviewRoundStatus;
import com.deepconnect.ats.common.constant.enums.InterviewRoundType;
import com.deepconnect.ats.common.constant.enums.ResumeOperationType;
import com.deepconnect.ats.common.utils.TimeUtil;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.InterviewCandidate;
import com.deepconnect.ats.model.InterviewRound;
import com.deepconnect.ats.repository.CandidateResumeRepository;
import com.deepconnect.ats.repository.InterviewRoundRepository;
import com.deepconnect.ats.repository.ScheduleJobQueryRepository;
import com.deepconnect.ats.repository.TaskRecordRepository;
import com.deepconnect.ats.service.resume.CandidateStatusCalculator;
import com.deepconnect.ats.service.resume.ResumeStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Lazy(false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ats.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class InterviewTimeTask {

    public static final String INTERVIEW_TIME_TASK_ID = "interview_time_task";

    private final TaskRecordRepository taskRecordRepository;
    private final ScheduleJobQueryRepository queryRepository;
    private final CandidateStatusCalculator calculator;
    private final ResumeStatusService resumeStatusService;
    private final CandidateResumeRepository candidateResumeRepository;
    private final InterviewRoundRepository interviewRoundRepository;

    @Scheduled(cron = "0 */5 * * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void interviewTimeTask() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>interview_time_task: start");
        try {
            taskRecordRepository.lockAllByTaskId(INTERVIEW_TIME_TASK_ID);
        } catch (PessimisticLockingFailureException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>email_task_schedule: conflict");
            return;
        }

        List<InterviewRound> scheduledRounds = queryRepository.findRoundsByStatus(
                List.of(InterviewRoundStatus.SCHEDULED));
        if (!scheduledRounds.isEmpty()) {
            List<InterviewRound> startedRounds = new ArrayList<>();
            List<InterviewRound> endRounds = new ArrayList<>();
            LocalDateTime time = TimeUtil.futureTimeInMinutes(1.5);
            for (InterviewRound rd : scheduledRounds) {
                if (rd.getFromTime() != null && !rd.getFromTime().isAfter(time)) {
                    rd.setStatus(InterviewRoundStatus.IN_PROGRESS);
                    startedRounds.add(rd);
                } else if (rd.getToTime() != null && !rd.getToTime().isAfter(time)) {
                    endRounds.add(rd);
                }
            }

            if (!endRounds.isEmpty()) {
                List<Long> roundIds = endRounds.stream().map(InterviewRound::getId).toList();
                List<InterviewCandidate> interviewCandidates = queryRepository
                        .findInterviewCandidatesByRoundsAndStatus(roundIds,
                                List.of(InterviewCandidateStatus.INTERVIEW_SENT.getValue()));
                Map<Long, InterviewRoundType> roundMap = new HashMap<>();
                for (InterviewRound r : endRounds) {
                    roundMap.put(r.getId(), InterviewRoundType.getByValue(r.getRoundType()));
                }
                Map<Long, CandidateResume> resumes = candidateResumeRepository.findAllById(
                                interviewCandidates.stream().map(InterviewCandidate::getCandidateResumeId)
                                        .collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(CandidateResume::getId, r -> r));

                List<CandidateResume> candidateResumes = new ArrayList<>();
                for (InterviewCandidate ic : interviewCandidates) {
                    CandidateStatusCalculator.StatusResult result = calculator.calculateForInterview(
                            ResumeOperationType.INTERVIEW_END_TIME_REACHED, roundMap.get(ic.getRoundId()));
                    CandidateResume resume = resumes.get(ic.getCandidateResumeId());
                    if (resume == null) {
                        continue;
                    }
                    resume.setStatus(result.status());
                    resume.setSubStatus(result.subStatus() == null ? null : result.subStatus().getCode());
                    candidateResumes.add(resume);
                    resumeStatusService.processManagementStatus(resume, null);
                }
                candidateResumeRepository.saveAll(candidateResumes);
            }

            List<InterviewRound> rounds = new ArrayList<>(startedRounds);
            rounds.addAll(endRounds);
            interviewRoundRepository.saveAll(rounds);
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>>>interview_time_task: end");
    }
}
