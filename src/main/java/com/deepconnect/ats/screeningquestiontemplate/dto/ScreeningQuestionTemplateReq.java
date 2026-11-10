package com.deepconnect.ats.screeningquestiontemplate.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestion;

import java.util.List;
import java.util.Set;

@Data
public class ScreeningQuestionTemplateReq {
    private String id;

    @Length(max = 100, message = "{field.evaluationPlan.name}{validation.lengthMax}")
    @NotBlank(message = "{field.evaluationPlan.name}{validation.notBlank}")
    private String name;

    @Length(max = 300, message = "{field.evaluationPlan.description}{validation.lengthMax}")
    private String description;

    private Boolean isPublic;

    private Boolean isSystemTemplate;

    @Valid
    private List<ScreeningQuestion> questions;

    private Set<String> sharedWithUserIds;

    private String ownerId;
}
