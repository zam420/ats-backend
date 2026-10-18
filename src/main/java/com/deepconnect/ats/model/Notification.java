package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "ats_notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer type;

    @Column(name = "trigger_obj_id", nullable = false)
    private Integer triggerObjId;

    @Column(nullable = false, length = 1024)
    private String message;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extend_param", columnDefinition = "json")
    private Map<String, Object> extendParam;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "notify_time", nullable = false)
    private LocalDateTime notifyTime;

    @Column(nullable = false)
    private Boolean task;

    @Column(nullable = false)
    private Boolean complete;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;
}
