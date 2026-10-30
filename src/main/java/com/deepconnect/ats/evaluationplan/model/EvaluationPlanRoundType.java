package com.deepconnect.ats.evaluationplan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
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
    @Length(max = 30, message = "{field.evaluationPlanRoundType.name}{validation.lengthMax}")
    @NotBlank(message = "{field.evaluationPlanRoundType.name}{validation.notBlank}")
    private String name;

    @Column(length = 100)
    @Length(max = 100, message = "{field.evaluationPlanRoundType.description}{validation.lengthMax}")
    private String description;

}
