package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ats_email_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsEmailRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "`to`", nullable = false, columnDefinition = "json")
    private List<String> to;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> cc;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> bcc;

    @Column(length = 1024)
    private String subject;

    @Column(nullable = false, columnDefinition = "longtext")
    private String body;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_models", nullable = false, columnDefinition = "json")
    private Object triggerModels;

    @Column(nullable = false)
    private Integer status;

    @Column(length = 225)
    @Builder.Default
    private String remark = "success";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "from_user_id")
    private Long fromUserId;

    @Column(name = "is_html_mail", nullable = false)
    @Builder.Default
    private Boolean isHtmlMail = false;

    @Column(name = "mail_type", nullable = false)
    private Integer mailType;

    @Column(name = "need_result", nullable = false)
    @Builder.Default
    private Boolean needResult = false;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
}
