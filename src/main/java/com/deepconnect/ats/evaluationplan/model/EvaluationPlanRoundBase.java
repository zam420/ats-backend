package com.deepconnect.ats.evaluationplan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.deepconnect.ats.common.model.BaseEntity;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class EvaluationPlanRoundBase extends BaseEntity {

    @Column(nullable = true, length = 30)
    @Length(max = 30, message = "{field.evaluationPlanRound.name}{validation.lengthMax}")
    private String name;

    @Column(nullable = true, length = 100)
    @Length(max = 100, message = "{field.evaluationPlanRound.description}{validation.lengthMax}")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roundType", referencedColumnName = "id", insertable = false, updatable = false)
    private EvaluationPlanRoundType evaluationPlanRoundType; 

    @Column(name = "roundType", nullable = false)
    @NotNull(message = "{field.evaluationPlanRound.roundType}{validation.notBlank}")
    private Integer roundType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> interviewers;

    @Column(name = "`order`", nullable = false)
    @NotNull(message = "{field.evaluationPlanRound.order}{validation.notBlank}")
    private Integer order;

}
