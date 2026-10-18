package com.deepconnect.ats.schedule;

import com.deepconnect.ats.model.TaskRecord;
import com.deepconnect.ats.repository.TaskRecordRepository;
import com.deepconnect.ats.service.NowcoderExamService;
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

@Slf4j
@Component
@Lazy(false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ats.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class NowcoderExamSchedule {
    private static final String TASK_ID = "exam_update_nowcoder_schedule";
    private static final DateTimeFormatter RECORD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final TaskRecordRepository taskRecordRepository;
    private final NowcoderExamService nowcoderExamService;

    @Scheduled(cron = "0 0,29 * * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void updateNowcoderTestSchedule() {
        log.info("===> update_nowcoder_test_schedule()");
        final TaskRecord taskRecord;
        try {
            List<TaskRecord> records = taskRecordRepository.lockAllByTaskId(TASK_ID);
            if (records.isEmpty()) return;
            taskRecord = records.get(0);
        } catch (PessimisticLockingFailureException e) {
            log.info("===> update_nowcoder_test_schedule(): conflict");
            return;
        }
        List<Long> examIds = nowcoderExamService.updateTestResult();
        log.info("update_nowcoder_test_schedule() exams: {}", examIds);
        taskRecord.setRecord(LocalDateTime.now(java.time.ZoneId.of(SchedulerConfig.ZONE)).format(RECORD_FORMAT));
        taskRecordRepository.save(taskRecord);
        log.info("===> update_nowcoder_test_schedule() end");
    }
}
