package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ats_resume_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "share_to", columnDefinition = "json")
    private List<Long> shareTo;

    @Column(nullable = false, length = 10240)
    private String comment;

    @CreationTimestamp
    @Column(name = "comment_at", nullable = false, updatable = false)
    private LocalDateTime commentAt;

    @Column(name = "candidate_resume_id", nullable = false)
    private Long candidateResumeId;

    @Column(name = "commentator_id", nullable = false)
    private Long commentatorId;
}
