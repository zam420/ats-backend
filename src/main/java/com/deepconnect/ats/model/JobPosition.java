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
@Table(name = "ats_job_position", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "project_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "recruitment_type", nullable = false)
    private Integer recruitmentType;

    @Column(name = "employment_type", nullable = false)
    private Integer employmentType;

    @Column(name = "job_type", nullable = false)
    private Integer jobType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "work_location", nullable = false, columnDefinition = "json")
    private List<Integer> workLocation;

    @Column(name = "job_desc", nullable = false, length = 5000)
    private String jobDesc;

    @Column(name = "job_req", nullable = false, length = 5000)
    private String jobReq;

    @Column(name = "hr_recruiter_id", nullable = false)
    private Long hrRecruiterId;

    @Column(name = "resume_inbox", nullable = false, length = 254)
    private String resumeInbox;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "publish_channel", columnDefinition = "json")
    private List<String> publishChannel;

    @Column(name = "created_by_id", nullable = false)
    private Long createdById;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extended_questions", columnDefinition = "json")
    private List<Map<String, Object>> extendedQuestions;

    @Column(name = "public_interview_feedback", nullable = false)
    @Builder.Default
    private Boolean publicInterviewFeedback = false;
}
