package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ats_candidate_project_experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProjectExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "candidate_resume_id", nullable = false)
    private Long candidateResumeId;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @Column(name = "up_to_now")
    @Builder.Default
    private Boolean upToNow = false;

    @Column(name = "project_name", length = 500)
    private String projectName;

    @Column(name = "project_role", length = 100)
    private String projectRole;

    @Column(name = "project_desc", length = 2000)
    private String projectDesc;

    @Column(name = "major_achievement", length = 3000)
    private String majorAchievement;

    @Column(name = "data_status", nullable = false)
    @Builder.Default
    private Integer dataStatus = 0;
}
