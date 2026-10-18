package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ats_candidate_work_experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateWorkExperience {

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

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(length = 100)
    private String title;

    @Column(name = "major_achievement", columnDefinition = "text")
    private String majorAchievement;

    @Column(name = "work_years")
    private Integer workYears;

    @Column(name = "data_status", nullable = false)
    @Builder.Default
    private Integer dataStatus = 0;
}
