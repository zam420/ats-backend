package com.deepconnect.ats.evaluationplan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.deepconnect.ats.common.model.BaseEntity;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRoundReqRes;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class EvaluationPlanRoundBase extends BaseEntity {

    @Column(nullable = true, length = 30)
    @Length(max = 30, message = "{field.evaluationPlanRound.name}{validation.lengthMax}")
    private String name;

    @Column(nullable = true, length = 100)
    @Length(max = 100, message = "{field.evaluationPlanRound.description}{validation.lengthMax}")
    private String description;

    @Column(name = "roundType", nullable = false)
    @NotNull(message = "{field.evaluationPlanRound.roundType}{validation.notBlank}")
    @Enumerated(EnumType.STRING)
    private EvaluationPlanRoundType roundType; 

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> interviewers;

    @Column(name = "`order`", nullable = false)
    @NotNull(message = "{field.evaluationPlanRound.order}{validation.notBlank}")
    private Integer order;

    protected EvaluationPlanRoundBase(EvaluationPlanRoundReqRes dto) {
        this.setPublicId(dto.getId());
        this.setName(dto.getName());
        this.setDescription(dto.getDescription());
        this.setRoundType(dto.getRoundType());
        this.setInterviewers(dto.getInterviewers());
        this.setOrder(dto.getOrder());
    }

    public static EvaluationPlanRoundBase from(EvaluationPlanRoundReqRes dto) {
        if(dto == null)
            return null;
        return new EvaluationPlanRoundBase(dto);
    }

}
