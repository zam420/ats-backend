package com.deepconnect.ats.schedule;

import com.deepconnect.ats.common.constant.enums.NotificationType;
import com.deepconnect.ats.model.AtsEmailRecord;
import com.deepconnect.ats.notification.model.Notification;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.rbac.model.Role;
import com.deepconnect.ats.rbac.service.RoleService;
import com.deepconnect.ats.repository.AtsEmailRecordRepository;
import com.deepconnect.ats.notification.repo.NotificationRepository;
import com.deepconnect.ats.repository.TaskRecordRepository;
import com.deepconnect.ats.user.repo.UserRepository;
import com.deepconnect.ats.service.mail.MailService;
import com.deepconnect.ats.rbac.model.UserRole;
import com.deepconnect.ats.rbac.repo.UserRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
public class EmailTaskSchedule {
    private static final String TASK_ID = "email_task_schedule";
    private final TaskRecordRepository taskRecordRepository;
    private final AtsEmailRecordRepository emailRepository;
    private final MailService mailService;
    private final RoleService roleService;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;


    @Scheduled(cron = "0 */5 * * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void emailTaskSchedule() {
        try {
            taskRecordRepository.lockAllByTaskId(TASK_ID);
        } catch (PessimisticLockingFailureException e) {
            log.debug(">>>>>>>>>>>>>>>>>>>>>email_task_schedule: conflict");
            return;
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>email_task_schedule: start");
        List<AtsEmailRecord> pending = emailRepository.findByStatus(0);
        if (!pending.isEmpty()) {
            List<User> notifyUsers = failureRecipients();
            List<Notification> notifications = new ArrayList<>();
            for (AtsEmailRecord mail : pending) {
                if (mail.getScheduledTime() != null && mail.getScheduledTime().isAfter(LocalDateTime.now())) continue;
                try {
                    mailService.doSend(mail);
                    mail.setStatus(1);
                } catch (Exception e) {
                    mail.setStatus(2);
                    mail.setRemark(e.getMessage() == null ? e.toString() : e.getMessage());
                    for (User user : notifyUsers) notifications.add(failureNotification(mail, user));
                }
            }
            emailRepository.saveAll(pending);
            if (!notifications.isEmpty()) notificationRepository.saveAll(notifications);
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>email_task_schedule: end");
    }

    private List<User> failureRecipients() {
        List<User> users = new ArrayList<>();
        for (String roleCode : List.of("sa", "hrd")) {
            Role role = roleService.findByCode(roleCode).orElse(null);
            if (role == null) continue;
            for (UserRole userRole : userRoleRepository.findByRoleId(role.getId())) {
                userRepository.findById(userRole.getUserId()).ifPresent(users::add);
            }
        }
        return users;
    }

    private Notification failureNotification(AtsEmailRecord mail, User user) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("mail_type", mail.getMailType());
        params.put("mail_triggers", mail.getTriggerModels());
        String fromName = mail.getFromUserId() == null ? "null" : userRepository.findById(mail.getFromUserId())
                .map(User::getName).orElse("null");
        return Notification.builder().userId(user.getId()).type(NotificationType.MAIL_SEND_RESULT.getValue())
                .triggerObjId(mail.getId().intValue())
                .message(fromName + " failed to send an email to " + mail.getTo() + " on " + mail.getCreatedAt() + ".")
                .extendParam(params).createTime(LocalDateTime.now()).notifyTime(LocalDateTime.now())
                .task(false).complete(false).build();
    }
}
