package com.deepconnect.ats.job.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import lombok.*;


@Entity
@Table(name = "job_experience_level_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExperienceLevelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(nullable = false, length = 30)
    @Length(max = 30, message = "{field.experienceLevelType.name}{validation.lengthMax}")
    @NotBlank(message = "{field.experienceLevelType.name}{validation.notBlank}")
    private String name;

    @Column(length = 100)
    @Length(max = 100, message = "{field.experienceLevelType.description}{validation.lengthMax}")
    private String description;

}
