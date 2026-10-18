package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ats_candidate_resume_forward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResumeForward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "forward_notes", length = 2048)
    private String forwardNotes;

    @Column(name = "feedback_notes", length = 2048)
    private String feedbackNotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "candidate_resume_id", nullable = false)
    private Long candidateResumeId;

    @Column(name = "resume_position_id")
    private Long resumePositionId;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    @Column(name = "create_by_id", nullable = false)
    private Long createById;

    @Column(name = "update_by_id", nullable = false)
    private Long updateById;
}
