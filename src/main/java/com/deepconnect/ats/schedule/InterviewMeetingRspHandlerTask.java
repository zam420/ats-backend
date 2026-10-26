package com.deepconnect.ats.schedule;

import com.deepconnect.ats.common.constant.enums.NotificationType;
import com.deepconnect.ats.common.utils.TimeUtil;
import com.deepconnect.ats.model.InterviewRound;
import com.deepconnect.ats.notification.model.Notification;
import com.deepconnect.ats.model.TaskRecord;
import com.deepconnect.ats.notification.repo.NotificationRepository;
import com.deepconnect.ats.repository.ScheduleJobQueryRepository;
import com.deepconnect.ats.repository.TaskRecordRepository;
import com.deepconnect.ats.service.TeamsMeetingScheduleService;
import com.deepconnect.ats.service.TeamsMeetingScheduleService.MeetingResponse;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Lazy(false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ats.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class InterviewMeetingRspHandlerTask {

    public static final String TASK_ID = "interview_meeting_rsp_handler_task";

    private final TaskRecordRepository taskRecordRepository;
    private final TeamsMeetingScheduleService teamsMeetingScheduleService;
    private final ScheduleJobQueryRepository queryRepository;
    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 */5 * * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void interviewMeetingRspHandlerTask() {
        TaskRecord taskRecord;
        List<MeetingResponse> scheduleRsp;
        try {
            taskRecord = taskRecordRepository.lockFirstByTaskId(TASK_ID).orElse(null);
            if (taskRecord == null) {
                log.warn(">>>>>>>>>>>>>>>>>>>>>>>interview_meeting_rsp_handler_task: task record not found");
                return;
            }
            scheduleRsp = teamsMeetingScheduleService.getMeetingScheduleRsp(
                    TimeUtil.parseDateTime(taskRecord.getRecord()));
        } catch (PessimisticLockingFailureException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>interview_meeting_rsp_handler_task: conflict");
            return;
        }

        if (scheduleRsp.isEmpty()) {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>interview_meeting_rsp_handler_task: schedule_rsp is empty");
            return;
        }
        // newest first; later entries overwrite earlier ones (same as the Python dict comprehension)
        Map<String, MeetingResponse> responseMap = new LinkedHashMap<>();
        for (MeetingResponse rsp : scheduleRsp) {
            responseMap.put(TeamsMeetingScheduleService.meetingIdConvert(rsp.eventId(), false), rsp);
        }
        List<String> meetingIds = new ArrayList<>();
        for (String key : responseMap.keySet()) {
            meetingIds.add(TeamsMeetingScheduleService.meetingIdConvert(key, true));
        }
        List<InterviewRound> rounds = queryRepository.findRoundsByTeamsMeetingIds(meetingIds);

        Map<String, Object[]> interviewMap = new LinkedHashMap<>();
        for (InterviewRound ir : rounds) {
            Object meetingId = ir.getTeamsMeeting() == null ? null : ir.getTeamsMeeting().get("meeting_id");
            interviewMap.put(ir.getRoundCode(), new Object[]{ir.getCreatedById(),
                    TeamsMeetingScheduleService.meetingIdConvert(String.valueOf(meetingId), false)});
        }
        List<Notification> notifications = new ArrayList<>();
        for (Map.Entry<String, Object[]> e : interviewMap.entrySet()) {
            MeetingResponse rsp = responseMap.get((String) e.getValue()[1]);
            if (rsp == null) {
                continue;
            }
            notifications.add(Notification.builder()
                    .userId((Long) e.getValue()[0])
                    .type(NotificationType.INTERVIEW_TEAMS_SCHEDULE_RSP.getValue())
                    .triggerObjId(0)
                    .extendParam(Map.of("round_code", e.getKey()))
                    .message(rsp.senderName() + " " + rsp.responseType() + " : " + rsp.subject())
                    .notifyTime(TimeUtil.now())
                    .task(false)
                    .complete(false)
                    .build());
        }
        notificationRepository.saveAll(notifications);
        LocalDateTime nextTime = TimeUtil.toDefaultZone(scheduleRsp.get(0).received()).plusSeconds(1);
        taskRecord.setRecord(TimeUtil.dateTimeFormat(nextTime));
        taskRecordRepository.save(taskRecord);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>interview_meeting_rsp_handler_task: end");
    }
}
