package com.deepconnect.ats.interviewplan.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "interview_plan_round_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewPlanRoundType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

}
