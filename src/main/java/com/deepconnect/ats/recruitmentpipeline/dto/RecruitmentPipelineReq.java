package com.deepconnect.ats.recruitmentpipeline.dto;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Set;

@Data
public class RecruitmentPipelineReq {
    private String id;

    @Length(max = 100, message = "{field.recruitmentPipeline.name}{validation.lengthMax}")
    @NotBlank(message = "{field.recruitmentPipeline.name}{validation.notBlank}")
    private String name;

    @Length(max = 300, message = "{field.recruitmentPipeline.description}{validation.lengthMax}")
    private String description;

    private Boolean isPublic;

    private Boolean isSystemTemplate;

    @Valid
    private List<RecruitmentPipelineRound> rounds;

    private Set<String> sharedWithUserIds;

    private String ownerId;
}
