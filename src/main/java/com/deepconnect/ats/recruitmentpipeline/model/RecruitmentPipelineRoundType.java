package com.deepconnect.ats.recruitmentpipeline.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import lombok.*;


@Entity
@Table(name = "recruitment_pipeline_round_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentPipelineRoundType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(nullable = false, length = 30)
    @Length(max = 30, message = "{field.recruitmentPipelineRoundType.name}{validation.lengthMax}")
    @NotBlank(message = "{field.recruitmentPipelineRoundType.name}{validation.notBlank}")
    private String name;

    @Column(length = 100)
    @Length(max = 100, message = "{field.recruitmentPipelineRoundType.description}{validation.lengthMax}")
    private String description;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isCustom = false;

}
