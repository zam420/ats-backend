package com.deepconnect.ats.job.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionReqRes;
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

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "question_set_version")
    private Integer questionSetVersion;

    protected JobScreeningQuestion(ScreeningQuestionReqRes dto, Long jobPositionId) {
        super(dto);
        this.jobPositionId = jobPositionId;
    }

    public static JobScreeningQuestion from(ScreeningQuestionReqRes dto, Long jobPositionId) {
        if(dto == null)
            return null;
        JobScreeningQuestion question = new JobScreeningQuestion(dto, jobPositionId);
        question.setActive(true);
        return question;
    }

    public static List<JobScreeningQuestion> from(List<ScreeningQuestionReqRes> entities, Long jobPositionId) {
        return entities.stream().map(dto -> JobScreeningQuestion.from(dto, jobPositionId)).toList();
    }

}
