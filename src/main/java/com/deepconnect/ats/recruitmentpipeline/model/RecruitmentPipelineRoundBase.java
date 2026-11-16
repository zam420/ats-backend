package com.deepconnect.ats.recruitmentpipeline.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.deepconnect.ats.common.model.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class RecruitmentPipelineRoundBase extends BaseEntity {

    @Column(nullable = true, length = 30)
    @Length(max = 30, message = "{field.recruitmentPipelineRound.name}{validation.lengthMax}")
    private String name;

    @Column(nullable = true, length = 100)
    @Length(max = 100, message = "{field.recruitmentPipelineRound.description}{validation.lengthMax}")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roundType", referencedColumnName = "id", insertable = false, updatable = false)
    private RecruitmentPipelineRoundType recruitmentPipelineRoundType; // The type of the round. Can be changed.

    @Column(name = "roundType", nullable = false)
    @NotNull(message = "{field.recruitmentPipelineRound.roundType}{validation.notBlank}")
    private Integer roundType;

    @Column(name = "`order`", nullable = false)
    @NotNull(message = "{field.recruitmentPipelineRound.order}{validation.notBlank}")
    private Integer order;

}
