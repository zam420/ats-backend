package com.deepconnect.ats.screeningquestionsform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

import com.deepconnect.ats.common.model.BaseEntity;
import com.deepconnect.ats.screeningquestionsform.enums.ScreeningQuestionType;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class ScreeningQuestionBase extends BaseEntity {

    @Column(nullable = false, length = 1000)
    @Length(max = 1000, message = "{field.screeningQuestion.label}{validation.lengthMax}")
    @NotBlank(message = "{field.screeningQuestion.label}{validation.notBlank}")
    private String label;//label is for display to candidates e.g. how is this question labaled for candidates

    @Column(length = 500)
    @Length(max = 500, message = "{field.screeningQuestion.description}{validation.lengthMax}")
    private String description;//description is for additional context or details about the question to be displayed to candidate

    //Options for checkbox, radio buttons, or dropdown questions
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = true)
    @Size(max = 20, message = "{field.screeningQuestion.options}{validation.maxSize}")
    private List<String> options;

    //Default option for checkbox, radio buttons, or dropdown questions
    @Column(length = 100)
    @Length(max = 100, message = "{field.screeningQuestion.defaultOption}{validation.lengthMax}")
    private String defaultOption;//description is for additional context or details about the question to be displayed to candidate

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isMandatory = false;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isDropDownMultiSelect = false;

    @Column(nullable = true)
    private Integer maxChars;

    @Column(name = "heading_level")
    @Min(1)
    @Max(3)
    private Integer headingLevel;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "{field.screeningQuestion.screeningQuestionType}{validation.notBlank}")
    @Column(nullable = false)
    private ScreeningQuestionType screeningQuestionType; 

    @Column(name = "`order`", nullable = false)
    @NotNull(message = "{field.screeningQuestion.order}{validation.notBlank}")
    private Integer order;

}
