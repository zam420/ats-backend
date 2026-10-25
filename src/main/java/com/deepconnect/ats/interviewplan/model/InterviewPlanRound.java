package com.deepconnect.ats.interviewplan.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.deepconnect.ats.common.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

@Entity
@Table(name = "interview_plan_round", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"interview_plan", "`order`"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewPlanRound extends BaseEntity {

    @Column(name = "round_type", nullable = false)
    private Integer roundType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> interviewers;

    @Column(name = "`order`", nullable = false)
    private Integer order;

    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewPlan", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private InterviewPlan interviewPlan;

    @Column(name = "interviewPlan", nullable = false)
    private Long interviewPlanId;
}
