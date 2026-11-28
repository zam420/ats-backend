package com.deepconnect.ats.candidatepipeline.model;

import java.time.Instant;

import org.hibernate.validator.constraints.Length;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidate_evaluation_feedback",
        uniqueConstraints = @UniqueConstraint(columnNames = { "candidate_evaluation_round_id", "interviewer_id" }))
@Getter
@Setter
@NoArgsConstructor
public class CandidateEvaluationFeedback extends BaseEntity {
    @NotNull
    @Column(name = "candidate_evaluation_round_id", nullable = false)
    private Long candidateEvaluationRoundId;

    @NotBlank
    @Column(name = "interviewer_id", nullable = false, length = 12)
    private String interviewerId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private CandidateEvaluationResult result;

    @Length(max = 1000)
    @Column(name = "overall_feedback", length = 1000)
    private String overallFeedback;

    @Length(max = 1000)
    @Column(length = 1000)
    private String strengths;

    @Length(max = 1000)
    @Column(name = "areas_for_improvement", length = 1000)
    private String areasForImprovement;

    @Column(nullable = false)
    private boolean submitted;

    @Column(name = "submitted_at")
    private Instant submittedAt;
}
