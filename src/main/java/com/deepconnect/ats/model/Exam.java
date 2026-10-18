package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "ats_exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_resume_id", nullable = false)
    private Long candidateResumeId;

    @Column(name = "job_position_id")
    private Long jobPositionId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private Integer source;

    @Column(name = "paper_id", nullable = false)
    private Integer paperId;

    @Column(name = "paper_name", nullable = false, length = 255)
    private String paperName;

    @Column(name = "test_candidate_id", nullable = false)
    private Integer testCandidateId;

    @Column(name = "test_url", nullable = false, length = 4096)
    private String testUrl;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "data_status", nullable = false)
    private Integer dataStatus;

    private Float score;

    @Column(name = "paper_score")
    private Float paperScore;

    @Column(name = "cheat_status")
    private Integer cheatStatus;

    @Column(name = "test_time")
    private LocalDateTime testTime;

    @Column(name = "web_report_url", length = 4096)
    private String webReportUrl;

    @Column(name = "result_url", length = 4096)
    private String resultUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_file", columnDefinition = "json")
    private Object resultFile;

    @Column(name = "begin_time")
    private LocalDateTime beginTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "hr_feedback")
    private Integer hrFeedback;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by_id", nullable = false)
    private Long createdById;
}
