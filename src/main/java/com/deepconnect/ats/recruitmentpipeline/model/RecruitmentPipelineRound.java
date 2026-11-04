package com.deepconnect.ats.recruitmentpipeline.model;

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
@Table(name = "recruitment_pipeline_round", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"recruitment_pipeline", "`order`"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentPipelineRound extends BaseEntity {

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

    @JsonBackReference("rounds")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentPipeline", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private RecruitmentPipeline recruitmentPipeline;

    @Column(name = "recruitmentPipeline", nullable = false)
    private Long recruitmentPipelineId;
}
