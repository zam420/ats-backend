package com.deepconnect.ats.jobapplication.model;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.deepconnect.ats.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "job_application_screening_question_answers")
@Getter
@Setter
@NoArgsConstructor
public class JobApplicationScreeningQuestionAnswer extends BaseEntity {
    @Column(name = "job_application_id", nullable = false)
    private Long jobApplicationId;
    @Column(name = "job_screening_question_id", nullable = false)
    private Long jobScreeningQuestionId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    private List<String> answer;
}
