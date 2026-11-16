package com.deepconnect.ats.screeningquestionsform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import lombok.*;


@Entity
@Table(name = "screening_question_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningQuestionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(nullable = false, length = 10)
    @Length(max = 10, message = "{field.job.screeningQuestion.type.code}{validation.lengthMax}")
    @NotBlank(message = "{field.job.screeningQuestion.type.code}{validation.notBlank}")
    private String code;

    @Column(nullable = false, length = 30)
    @Length(max = 30, message = "{field.job.screeningQuestion.type.name}{validation.lengthMax}")
    @NotBlank(message = "{field.job.screeningQuestion.type.name}{validation.notBlank}")
    private String name;

    @Column(length = 100)
    @Length(max = 100, message = "{field.job.screeningQuestion.type.description}{validation.lengthMax}")
    private String description;

}
