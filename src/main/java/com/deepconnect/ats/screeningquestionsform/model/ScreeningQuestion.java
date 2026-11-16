package com.deepconnect.ats.screeningquestionsform.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "screening_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ScreeningQuestion extends ScreeningQuestionBase {

    @JsonBackReference("questions")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "screeningQuestionsForm", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private ScreeningQuestionsForm screeningQuestionsForm;

    @Column(name = "screeningQuestionsForm", nullable = false)
    private Long screeningQuestionsFormId;

}
