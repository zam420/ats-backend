package com.deepconnect.ats.schedule;

import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.dto.DuplicationCheckBody;
import com.deepconnect.ats.model.ResumeDuplicationTask;
import com.deepconnect.ats.model.TaskRecord;
import com.deepconnect.ats.repository.ResumeDuplicationTaskRepository;
import com.deepconnect.ats.repository.TaskRecordRepository;
import com.deepconnect.ats.service.CandidateDuplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Component
@Lazy(false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ats.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ResumeDuplicationSchedule {
    private static final String TASK_ID = "resume_duplication_schedule";
    private static final DateTimeFormatter RECORD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final TaskRecordRepository taskRecordRepository;
    private final ResumeDuplicationTaskRepository duplicationTaskRepository;
    private final CandidateDuplicationService candidateDuplicationService;

    @Scheduled(cron = "0 */10 * * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void resumeDuplicationSchedule() {
        log.info("===> resume_duplication_schedule()");
        final TaskRecord taskRecord;
        try {
            List<TaskRecord> records = taskRecordRepository.lockAllByTaskId(TASK_ID);
            if (records.isEmpty()) return;
            taskRecord = records.get(0);
            log.info("===> resume_duplication_schedule() resumes: {}", taskRecord.getRecord());
        } catch (PessimisticLockingFailureException e) {
            log.debug("===> resume_duplication_schedule(): conflict");
            return;
        }
        runCheckTasks();
        taskRecord.setRecord(LocalDateTime.now(java.time.ZoneId.of(SchedulerConfig.ZONE)).format(RECORD_FORMAT));
        taskRecordRepository.save(taskRecord);
        log.info("===> resume_duplication_schedule() end");
    }

    private void runCheckTasks() {
        List<ResumeDuplicationTask> tasks = duplicationTaskRepository.findAll();
        Set<Integer> resumeIds = new TreeSet<>();
        for (ResumeDuplicationTask task : tasks) resumeIds.add(task.getResumeId());
        log.info("===> resume_duplication_schedule _run_check_task(): {}", resumeIds);
        for (Integer resumeId : resumeIds) runCheckTask(resumeId);
    }

    private void runCheckTask(Integer resumeId) {
        try {
            candidateDuplicationService.checkResumeDuplication(new DuplicationCheckBody(null, resumeId.longValue()));
        } catch (CustomException e) {
            // if (e.getCode() == CandidateDuplicationService.RESUME_NOT_FOUND_CODE) {
            //     log.info("===> resume_duplication_schedule resume not found: {}", resumeId);
            // } else {
            throw e;
            // }
        }
        duplicationTaskRepository.deleteAll(duplicationTaskRepository.findByResumeId(resumeId));
        log.info("===> resume_duplication_schedule resume complete: {}", resumeId);
    }
}
