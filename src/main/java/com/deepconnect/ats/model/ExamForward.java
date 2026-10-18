package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ats_exam_forward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamForward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "feedback_notes", length = 2048)
    private String feedbackNotes;

    @Column(name = "data_status", nullable = false)
    private Integer dataStatus;

    @Column(name = "candidate_resume_id", nullable = false)
    private Long candidateResumeId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;
}
