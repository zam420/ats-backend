package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "ats_interview_rounds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_id", nullable = false)
    private Long interviewId;

    @Column(name = "round_code", nullable = false, length = 32)
    private String roundCode;

    @Column(name = "`order`", nullable = false)
    private Integer order;

    @Column(name = "round_type", nullable = false)
    private Integer roundType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Long> interviewers;

    @Column(name = "from_time")
    private LocalDateTime fromTime;

    @Column(name = "to_time")
    private LocalDateTime toTime;

    @Column(name = "is_online")
    private Boolean isOnline;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "teams_meeting", columnDefinition = "json")
    private Map<String, Object> teamsMeeting;

    @Column(name = "interview_location", length = 100)
    private String interviewLocation;

    @Column(name = "info_interviewer")
    private Boolean infoInterviewer;

    @Column(name = "info_candidate")
    private Boolean infoCandidate;

    @Column(name = "email_template_id")
    private Long emailTemplateId;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by_id", nullable = false)
    private Long createdById;

    @Column(name = "modified_by_id", nullable = false)
    private Long modifiedById;
}
