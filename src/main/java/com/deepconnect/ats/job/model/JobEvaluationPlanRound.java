package com.deepconnect.ats.job.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundBase;

@Entity
@Table(name = "job_evaluation_plan_round", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"evaluation_plan", "`order`"})
})
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
}
