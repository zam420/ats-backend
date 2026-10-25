package com.deepconnect.ats.interviewplan.model;

import jakarta.persistence.*;
import lombok.*;

import com.deepconnect.ats.common.model.OperatableBaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Table(name = "interview_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewPlan extends OperatableBaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "work_location", nullable = false)
    private Integer workLocation;

    @Column(length = 100)
    private String notes;

    @Column(nullable = false)
    private Integer status;

    @JsonManagedReference
    @OneToMany(mappedBy = "interviewPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewPlanRound> rounds;

}
