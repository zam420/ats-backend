package com.deepconnect.ats.recruitmentpipeline.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.deepconnect.ats.common.model.BaseEntity;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRoundReqRes;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class RecruitmentPipelineRoundBase extends BaseEntity {

    @Column(nullable = true, length = 30)
    @Length(max = 30, message = "{field.recruitmentPipelineRound.name}{validation.lengthMax}")
    private String name;

    @Column(nullable = true, length = 100)
    @Length(max = 100, message = "{field.recruitmentPipelineRound.description}{validation.lengthMax}")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "roundType", nullable = false)
    @NotNull(message = "{field.recruitmentPipelineRound.roundType}{validation.notBlank}")
    private RecruitmentPipelineRoundType roundType;

    @Column(name = "`order`", nullable = false)
    @NotNull(message = "{field.recruitmentPipelineRound.order}{validation.notBlank}")
    private Integer order;

    protected RecruitmentPipelineRoundBase(RecruitmentPipelineRoundReqRes dto) {
        this.setPublicId(dto.getId());
        this.setName(dto.getName());
        this.setDescription(dto.getDescription());
        this.setRoundType(dto.getRoundType());
        this.setOrder(dto.getOrder());
    }

    public static RecruitmentPipelineRoundBase from(RecruitmentPipelineRoundReqRes dto) {
        if(dto == null)
            return null;
        return new RecruitmentPipelineRoundBase(dto);
    }

}
