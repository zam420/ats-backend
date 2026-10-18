package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreateTimeDesc(Long userId);
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    long countByUserIdAndCompleteFalse(Long userId);
}
