package com.deepconnect.ats.candidatepipeline.model;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.common.model.BaseEntity;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidate_evaluation_round")
@Getter
@Setter
@NoArgsConstructor
public class CandidateEvaluationRound extends BaseEntity {
    @NotNull
    @Column(name = "job_application_id", nullable = false)
    private Long jobApplicationId;

    @Column(name = "plan_round_id")
    private Long planRoundId;

    @NotNull
    @Column(name = "round_order", nullable = false)
    private Integer order;

    @NotBlank
    @Length(max = 30)
    @Column(nullable = false, length = 30)
    private String name;

    @Length(max = 100)
    @Column(length = 100)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "round_type", nullable = false, length = 30)
    private EvaluationPlanRoundType roundType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> interviewers;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CandidateEvaluationStatus status = CandidateEvaluationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private CandidateEvaluationResult result;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @AssertTrue
    public boolean isResultConsistent() {
        return (status == CandidateEvaluationStatus.COMPLETED) == (result != null);
    }
}
