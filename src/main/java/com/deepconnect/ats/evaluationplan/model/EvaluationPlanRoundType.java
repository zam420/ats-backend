package com.deepconnect.ats.evaluationplan.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "evaluation_plan_round_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationPlanRoundType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 100)
    private String description;

}
