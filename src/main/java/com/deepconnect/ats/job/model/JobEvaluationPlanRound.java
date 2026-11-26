package com.deepconnect.ats.job.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRoundReqRes;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundBase;

@Entity
@Table(name = "job_evaluation_plan_round")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobEvaluationPlanRound extends EvaluationPlanRoundBase {

    @JsonBackReference("evaluation_plan_rounds")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jobPosition", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private JobPosition jobPosition;

    @Column(name = "jobPosition", nullable = false)
    private Long jobPositionId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "plan_version")
    private Integer planVersion;

    protected JobEvaluationPlanRound(EvaluationPlanRoundReqRes dto, Long jobPositionId, Boolean active) {
        super(dto);
        this.jobPositionId = jobPositionId;
        this.active = active;
    }

    public static JobEvaluationPlanRound from(EvaluationPlanRoundReqRes dto, Long jobPositionId, Boolean active) {
        if(dto == null)
            return null;
        return new JobEvaluationPlanRound(dto, jobPositionId, active);
    }

    public static List<JobEvaluationPlanRound> from(List<EvaluationPlanRoundReqRes> entities, Long jobPositionId,Boolean active) {
        return entities.stream().map(dto -> {
            JobEvaluationPlanRound round = JobEvaluationPlanRound.from(dto, jobPositionId, active);
            return round;
        }).toList();
    }
    
}
