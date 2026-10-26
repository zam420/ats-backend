package com.deepconnect.ats.schedule;

import com.deepconnect.ats.common.constant.enums.CandidateStatusCodes;
import com.deepconnect.ats.common.constant.enums.CandidateSubStatus;
import com.deepconnect.ats.common.utils.TimeUtil;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.Tag;
import com.deepconnect.ats.model.TaskRecord;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.repository.ScheduleJobQueryRepository;
import com.deepconnect.ats.repository.TaskRecordRepository;
import com.deepconnect.ats.user.repo.UserRepository;
import com.deepconnect.ats.service.resume.CandidateResumeAggregate;
import com.deepconnect.ats.service.resume.ResumeAutoTagService;
import com.deepconnect.ats.service.resume.ResumeOperation;
import com.deepconnect.ats.service.resume.ResumeStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Lazy(false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ats.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class CandidateResumeSchedule {

    public static final String ONBOARDING_TIME_WATCH_DOG = "onboarding_time_watch_dog";
    public static final String AUTO_TAG_RESUMES_FOR_FRESH_RESUMES = "auto_tag_resumes_for_fresh_resumes";
    public static final String AUTO_TAG_RESUMES_FOR_FRESH_TAGS = "auto_tag_resumes_for_fresh_tags";
    private static final int BATCH_SIZE = 50;

    private final TaskRecordRepository taskRecordRepository;
    private final ResumeStatusService resumeStatusService;
    private final ResumeAutoTagService resumeAutoTagService;
    private final ScheduleJobQueryRepository queryRepository;
    private final UserRepository userRepository;

    @Value("${ats.mail.username:}")
    private String systemUserEmail;

    // 每天凌晨2点
    @Scheduled(cron = "0 0 2 * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void onboardingTimeWatchDog() {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>candidate resume schedule#onboarding_time_watch_dog start");
        try {
            taskRecordRepository.lockFirstByTaskId(ONBOARDING_TIME_WATCH_DOG);
        } catch (PessimisticLockingFailureException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>candidate resume schedule#onboarding_time_watch_dog: conflict");
            return;
        }
        List<CandidateResume> ongoing = resumeStatusService.getResumeByStatus(
                CandidateStatusCodes.ONBOARDING_MANAGEMENT, CandidateSubStatus.ONBOARDING_ONGOING.getCode());
        LocalDate today = TimeUtil.now().toLocalDate();
        if (!ongoing.isEmpty()) {
            List<CandidateResume> onboarded = new ArrayList<>();
            for (CandidateResume resume : ongoing) {
                if (resume.getExpectOnboardingDate() != null && !resume.getExpectOnboardingDate().isAfter(today)) {
                    onboarded.add(resume);
                }
            }
            User systemUser = userRepository.findByEmail(systemUserEmail).orElse(null);
            if (systemUser == null && !onboarded.isEmpty()) {
                log.warn("system virtual user {} not found, skip onboarding update", systemUserEmail);
            } else {
                resumeStatusService.updateResumeStatusByOperate(systemUser, onboarded,
                        ResumeOperation.onboardingTimeReached());
            }
        }
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>candidate resume schedule#onboarding_time_watch_dog: finished");
    }

    // 每两小时执行一次
    @Scheduled(cron = "0 0 */2 * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void tagResumesForFreshResumes() {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>candidate resume schedule#tag_resumes_for_fresh_resumes start");
        TaskRecord taskRecord = lockRecord(AUTO_TAG_RESUMES_FOR_FRESH_RESUMES,
                "candidate resume schedule#tag_resumes_for_fresh_resumes");
        if (taskRecord == null) {
            return;
        }
        LocalDateTime lastTaskTime = TimeUtil.parseDateTime(taskRecord.getRecord());
        List<Tag> allTags = queryRepository.findAllTags();
        LocalDateTime latestUpdateTime = null;
        int offset = 0;
        while (true) {
            List<CandidateResume> page = queryRepository.findResumesUpdatedAfter(lastTaskTime, offset, BATCH_SIZE);
            if (page.isEmpty()) {
                break;
            }
            offset += page.size();
            List<CandidateResumeAggregate> aggregates = resumeStatusService.buildAggregates(page);
            resumeAutoTagService.autoTagResumes(aggregates, allTags);
            LocalDateTime updatedAt = page.get(0).getUpdatedAt();
            if (latestUpdateTime == null || (updatedAt != null && updatedAt.isAfter(latestUpdateTime))) {
                latestUpdateTime = updatedAt;
            }
            page.forEach(queryRepository::detach);
            if (page.size() < BATCH_SIZE) {
                break;
            }
        }
        if (latestUpdateTime != null) {
            taskRecord.setRecord(TimeUtil.dateTimeFormat(latestUpdateTime));
            taskRecordRepository.save(taskRecord);
        }
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>candidate resume schedule#tag_resumes_for_fresh_resumes: finished");
    }

    // 每天凌晨1点
    @Scheduled(cron = "0 0 1 * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void tagResumesForFreshTags() {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>candidate resume schedule#tag_resumes_for_fresh_tags start");
        TaskRecord taskRecord = lockRecord(AUTO_TAG_RESUMES_FOR_FRESH_TAGS,
                "candidate resume schedule#tag_resumes_for_fresh_tags");
        if (taskRecord == null) {
            return;
        }
        LocalDateTime lastTaskTime = TimeUtil.parseDateTime(taskRecord.getRecord());
        List<Tag> freshTags = queryRepository.findTagsUpdatedAfter(lastTaskTime);
        if (!freshTags.isEmpty()) {
            int offset = 0;
            while (true) {
                List<CandidateResume> page = queryRepository.findResumesUpdatedAfter(null, offset, BATCH_SIZE);
                if (page.isEmpty()) {
                    break;
                }
                offset += page.size();
                resumeAutoTagService.autoTagResumes(resumeStatusService.buildAggregates(page), freshTags);
                page.forEach(queryRepository::detach);
                if (page.size() < BATCH_SIZE) {
                    break;
                }
            }
            taskRecord.setRecord(TimeUtil.dateTimeFormat(freshTags.get(0).getUpdatedAt()));
            taskRecordRepository.save(taskRecord);
        }
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>candidate resume schedule#tag_resumes_for_fresh_tags: finished");
    }

    private TaskRecord lockRecord(String taskId, String name) {
        try {
            TaskRecord record = taskRecordRepository.lockFirstByTaskId(taskId).orElse(null);
            if (record == null) {
                log.warn(">>>>>>>>>>>>>>>>>>>>>>>{}: task record {} not found", name, taskId);
            }
            return record;
        } catch (PessimisticLockingFailureException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>{}: conflict", name);
            return null;
        }
    }
}
