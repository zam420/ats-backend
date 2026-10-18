package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "ats_interview_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_result", nullable = false)
    private Integer interviewResult;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "round_specific_feedback", columnDefinition = "json")
    private Map<String, Object> roundSpecificFeedback;

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

    @Column(name = "interview_candidate_id", nullable = false, unique = true)
    private Long interviewCandidateId;

    @Column(name = "interview_round_id", nullable = false)
    private Long interviewRoundId;
}
