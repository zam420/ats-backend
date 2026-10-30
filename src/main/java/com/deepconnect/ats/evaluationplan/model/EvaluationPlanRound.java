package com.deepconnect.ats.evaluationplan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.deepconnect.ats.common.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

@Entity
@Table(name = "evaluation_plan_round", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"evaluation_plan", "`order`"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationPlanRound extends BaseEntity {

    @Column(nullable = true, length = 30)
    @Length(max = 30, message = "{field.evaluationPlanRound.name}{validation.lengthMax}")
    private String name;

    @Column(nullable = true, length = 100)
    @Length(max = 100, message = "{field.evaluationPlanRound.description}{validation.lengthMax}")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roundType", referencedColumnName = "id", insertable = false, updatable = false)
    private EvaluationPlanRoundType evaluationPlanRoundType; // By default, the user who created this record. Can be changed.

    @Column(name = "roundType", nullable = false)
    @NotNull(message = "{field.evaluationPlanRound.roundType}{validation.notBlank}")
    private Integer roundType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> interviewers;

    @Column(name = "`order`", nullable = false)
    @NotNull(message = "{field.evaluationPlanRound.order}{validation.notBlank}")
    private Integer order;

    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluationPlan", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private EvaluationPlan evaluationPlan;

    @Column(name = "evaluationPlan", nullable = false)
    private Long evaluationPlanId;
}
