package com.deepconnect.ats.screeningquestionsform.model;

import jakarta.persistence.*;

import java.util.List;

import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRoundReqRes;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRound;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionReqRes;
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

    protected ScreeningQuestion(ScreeningQuestionReqRes dto, Long screeningQuestionsFormId) {
        super(dto);
        this.screeningQuestionsFormId = screeningQuestionsFormId;
    }

    public static ScreeningQuestion from(ScreeningQuestionReqRes dto, Long screeningQuestionsFormId) {
        if(dto == null)
            return null;
        return new ScreeningQuestion(dto, screeningQuestionsFormId);
    }

    public static List<ScreeningQuestion> from(List<ScreeningQuestionReqRes> entities, Long screeningQuestionsFormId) {
        return entities.stream().map(dto -> ScreeningQuestion.from(dto, screeningQuestionsFormId)).toList();
    }

}
