package com.deepconnect.ats.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.repository.NotificationRepository;

import java.util.Map;

/** Port of permission_validate(has_permission_to=notification): only the owner may touch a notification. */
@Component("notificationAccess")
@RequiredArgsConstructor
public class NotificationAccess {

    private final NotificationRepository notificationRepository;

    public boolean canAccess(Map<String, Object> body) {
        if (body == null) {
            return false;
        }
        Object id = body.get("notification_id") != null ? body.get("notification_id") : body.get("id");
        return id != null && canAccess(Long.valueOf(id.toString()));
    }

    public boolean canAccess(Long id) {
        Long userId = AuthService.getLoggedInUserId();
        if (userId == null || id == null) {
            return false;
        }
        return notificationRepository.findById(id)
                .map(n -> userId.equals(n.getUserId()))
                .orElse(false);
    }
}
