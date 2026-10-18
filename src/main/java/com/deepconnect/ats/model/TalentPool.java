package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ats_talent_pool")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalentPool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_resume_id", nullable = false, unique = true)
    private Long candidateResumeId;

    @Column(name = "pooled_reason", nullable = false)
    private Integer pooledReason;

    @Column(length = 500)
    private String comments;

    @CreationTimestamp
    @Column(name = "pooled_at", nullable = false, updatable = false)
    private LocalDateTime pooledAt;

    @Column(name = "pooled_by_id", nullable = false)
    private Long pooledById;
}
