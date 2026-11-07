package com.deepconnect.ats.job.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

import lombok.*;


@Entity
@Table(name = "job_candidate_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCandidateQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(nullable = false, length = 30)
    @Length(max = 30, message = "{field.job.candidateQuestion.label}{validation.lengthMax}")
    @NotBlank(message = "{field.job.candidateQuestion.label}{validation.notBlank}")
    private String label;

    @Column(length = 100)
    @Length(max = 100, message = "{field.job.candidateQuestion.description}{validation.lengthMax}")
    private String description;

    //Options for checkbox, radio buttons, or dropdown questions
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = true)
    @Size(max = 20, message = "{field.job.candidateQuestion.options}{validation.maxSize}")
    private List<String> options;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isMandatory = false;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isDropDownMultiSelect = false;

    @Column(nullable = true)
    private Integer maxChars;

}
