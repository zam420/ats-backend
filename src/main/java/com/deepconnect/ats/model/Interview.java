package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ats_interview_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_job_id", nullable = false)
    private Long targetJobId;

    @Column(name = "created_by_id", nullable = false)
    private Long createdById;

    @Column(name = "modified_by_id", nullable = false)
    private Long modifiedById;

    @Column(name = "interview_type", nullable = false)
    private Integer interviewType;

    @Column(name = "interview_template_id")
    private Long interviewTemplateId;

    @Column(name = "interview_status", nullable = false)
    @Builder.Default
    private Integer interviewStatus = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
