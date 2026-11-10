package com.deepconnect.ats.screeningquestiontemplate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.deepconnect.ats.common.model.BaseEntity;

import lombok.*;


@Entity
@Table(name = "screening_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningQuestion  extends BaseEntity {

    @Column(nullable = true, length = 30)
    @Length(max = 30, message = "{field.screeningQuestion.name}{validation.lengthMax}")
    private String name;//name is for user to remember this question

    @Column(nullable = false, length = 30)
    @Length(max = 30, message = "{field.screeningQuestion.label}{validation.lengthMax}")
    @NotBlank(message = "{field.screeningQuestion.label}{validation.notBlank}")
    private String label;//label is for display to candidates e.g. how is this question labaled for candidates

    @Column(length = 100)
    @Length(max = 100, message = "{field.screeningQuestion.description}{validation.lengthMax}")
    private String description;//description is for additional context or details about the question to be displayed to candidate

    //Options for checkbox, radio buttons, or dropdown questions
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = true)
    @Size(max = 20, message = "{field.screeningQuestion.options}{validation.maxSize}")
    private List<String> options;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isMandatory = false;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isDropDownMultiSelect = false;

    @Column(nullable = true)
    private Integer maxChars;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screeningQuestionType", referencedColumnName = "id", insertable = false, updatable = false)
    private ScreeningQuestionType screeningQuestionType; 

    @Column(name = "screeningQuestionType", nullable = false)
    @NotNull(message = "{field.screeningQuestion.screeningQuestionType}{validation.notBlank}")
    private Integer screeningQuestionTypeId;

    @Column(name = "`order`", nullable = false)
    @NotNull(message = "{field.screeningQuestion.order}{validation.notBlank}")
    private Integer order;

    @JsonBackReference("questions")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "screeningQuestionTemplate", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private ScreeningQuestionTemplate screeningQuestionTemplate;

    @Column(name = "screeningQuestionTemplate", nullable = false)
    private Long screeningQuestionTemplateId;

}
