package com.deepconnect.ats.schedule;

import com.deepconnect.ats.common.enums.ResumeInbox;
import com.deepconnect.ats.dto.CandidateResumeSaveReq;
import com.deepconnect.ats.model.ResumeDuplicationTask;
import com.deepconnect.ats.model.TaskRecord;
import com.deepconnect.ats.repository.ResumeDuplicationTaskRepository;
import com.deepconnect.ats.repository.TaskRecordRepository;
import com.deepconnect.ats.service.CandidateResumeService;
import com.deepconnect.ats.service.mail.MailService;
import com.deepconnect.ats.service.mail.ResumeEmail;
import com.deepconnect.ats.service.resumeparser.ResumeEmailParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Lazy(false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ats.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class EmailResumeHandlerTask {
    private static final String TASK_ID = "email_resume_parser";
    private static final DateTimeFormatter RECORD_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final TaskRecordRepository taskRecordRepository;
    private final MailService mailService;
    private final ResumeEmailParserService parserService;
    private final CandidateResumeService candidateResumeService;
    private final ResumeDuplicationTaskRepository duplicationTaskRepository;

    @Scheduled(cron = "0 */20 * * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void emailResumeHandler() {
        log.info(">>>>>>>>>>>>>>>>>>>>>email_resume_parser start");
        List<ResumeEmail> emails;
        try {
            List<TaskRecord> records = taskRecordRepository.lockAllByTaskId(TASK_ID);
            Map<String, TaskRecord> byInbox = new HashMap<>();
            for (TaskRecord record : records) byInbox.put(record.getSubId(), record);
            emails = fetchEmails(byInbox);
        } catch (PessimisticLockingFailureException e) {
            log.debug(">>>>>>>>>>>>>>>>>>>>>email_resume_parser: conflict");
            return;
        }
        if (emails.isEmpty()) {
            log.info(">>>>>>>>>>>>>>>>>>>>>email_resume_parser: mails is empty");
            return;
        }

        List<CandidateResumeSaveReq> forms = new ArrayList<>();
        for (ResumeEmail email : emails) {
            try { forms.addAll(parserService.parse(email)); }
            catch (Exception e) {
                String error=e.getMessage()==null?e.toString():e.getMessage();
                log.error("email_resume_handler_task mail parse failed,mail:{},error:{}", email.subject(), error);
            }
        }
        for (CandidateResumeSaveReq form : forms) {
            try {
                form.getCandidateResume().setDataStatus(1);
                Long id = candidateResumeService.saveCandidateResume(form);
                try { duplicationTaskRepository.save(ResumeDuplicationTask.builder().resumeId(id.intValue()).build()); }
                catch (Exception e) { log.error("save ResumeDuplicationTask error: {}", e.getMessage()); }
            } catch (Exception e) {
                log.error("email_resume_handler_task save candidate resumes failed,candidate_resume_form:{},error:{}",
                        form, e.getMessage()==null?e.toString():e.getMessage());
            }
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>email_resume_parser end");
    }

    private List<ResumeEmail> fetchEmails(Map<String, TaskRecord> taskRecords) {
        List<ResumeEmail> emails = new ArrayList<>();
        for (ResumeInbox inbox : ResumeInbox.values()) {
            TaskRecord record = taskRecords.get(inbox.getValue());
            if (record == null) continue;
            LocalDateTime from = parseRecordTime(record.getRecord());
            List<ResumeEmail> inboxEmails = mailService.getEmails(inbox.getValue(), from);
            if (!inboxEmails.isEmpty()) {
                ResumeEmail newest = inboxEmails.get(0);
                if (newest.receivedAt() != null) {
                    LocalDateTime nextTime = newest.receivedAt().atZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                            .toLocalDateTime().plusSeconds(1);
                    record.setRecord(RECORD_TIME.format(nextTime));
                    taskRecordRepository.save(record);
                }
                emails.addAll(inboxEmails);
            }
        }
        return emails;
    }

    private LocalDateTime parseRecordTime(String value) {
        try { return LocalDateTime.parse(value, RECORD_TIME); }
        catch (Exception ignored) { return LocalDateTime.parse(value); }
    }
}
