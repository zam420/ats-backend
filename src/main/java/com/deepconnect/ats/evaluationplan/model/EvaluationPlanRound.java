package com.deepconnect.ats.evaluationplan.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "evaluation_plan_round", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"evaluation_plan", "`order`"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EvaluationPlanRound extends EvaluationPlanRoundBase {

    @JsonBackReference("rounds")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluationPlan", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private EvaluationPlan evaluationPlan;

    @Column(name = "evaluationPlan", nullable = false)
    private Long evaluationPlanId;
}
