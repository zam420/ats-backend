package com.deepconnect.ats.job.dto;

import lombok.Data;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

@Data
public class JobPositionReq {

    private String id;
    
    @NotBlank(message = "{field.job.title}{validation.notBlank}")
    @Length(max = 100, message = "{field.job.title}{validation.lengthMax}")
    private String title;

    @NotBlank(message = "{field.job.description}{validation.notBlank}")
    @Length(max = 10000, message = "{field.job.description}{validation.lengthMax}")
    private String description;

    @NotBlank(message = "{field.job.mandatoryRequirements}{validation.notBlank}")
    @Length(max = 5000, message = "{field.job.mandatoryRequirements}{validation.lengthMax}")
    private String mandatoryRequirements;

    @Length(max = 5000, message = "{field.job.preferredRequirements}{validation.lengthMax}")
    private String preferredRequirements;

    @NotNull(message = "{field.job.employmentType}{validation.notBlank}")
    private Integer employmentType;

    @NotNull(message = "{field.job.workArrangementType}{validation.notBlank}")
    private Integer workArrangementType;

    @NotEmpty(message = "{field.job.workLocation}{validation.notEmpty}")
    private List<Integer> workLocation;

    @NotEmpty(message = "{field.job.recruiters}{validation.notEmpty}")
    private List<String> recruiters;

    @NotEmpty(message = "{field.job.hiringManagers}{validation.notEmpty}")
    private List<String> hiringManagers;

    @NotNull(message = "{field.job.recruitmentPipeline}{validation.notBlank}")
    private String recruitmentPipeline;

    @NotNull(message = "{field.job.evaluationPlan}{validation.notBlank}")
    private String evaluationPlan;

}
