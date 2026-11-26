package com.deepconnect.ats.evaluationplan.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRoundReqRes;
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

    protected EvaluationPlanRound(EvaluationPlanRoundReqRes dto, Long evaluationPlanId) {
        super(dto);
        this.evaluationPlanId = evaluationPlanId;
    }

    public static EvaluationPlanRound from(EvaluationPlanRoundReqRes dto, Long evaluationPlanId) {
        if(dto == null)
            return null;
        return new EvaluationPlanRound(dto, evaluationPlanId);
    }

    public static List<EvaluationPlanRound> from(List<EvaluationPlanRoundReqRes> entities, Long evaluationPlanId) {
        return entities.stream().map(dto -> EvaluationPlanRound.from(dto, evaluationPlanId)).toList();
    }
}
