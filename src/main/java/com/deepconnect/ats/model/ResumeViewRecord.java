package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ats_resume_view_record", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"candidate_resume_id", "view_by"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeViewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_resume_id", nullable = false)
    private Long candidateResumeId;

    @Column(name = "view_by", nullable = false)
    private Long viewBy;

    @CreationTimestamp
    @Column(name = "view_at", nullable = false, updatable = false)
    private LocalDateTime viewAt;
}
