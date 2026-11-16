package com.deepconnect.ats.job.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;
import lombok.experimental.SuperBuilder;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionBase;


@Entity
@Table(name = "job_screening_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobScreeningQuestion extends ScreeningQuestionBase {

    @JsonBackReference("screeningQuestions")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jobPosition", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private JobPosition jobPosition;

    @Column(name = "jobPosition", nullable = false)
    private Long jobPositionId;

}
