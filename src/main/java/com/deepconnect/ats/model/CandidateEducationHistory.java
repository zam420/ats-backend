package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ats_candidate_education_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateEducationHistory {

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

    @Column(length = 100)
    private String university;

    @Column(length = 100)
    private String school;

    @Column(name = "lab_name", length = 200)
    private String labName;

    private Integer degree;

    @Column(length = 100)
    private String major;

    @Column(name = "performance_rank")
    private Integer performanceRank;

    @Column(name = "data_status", nullable = false)
    @Builder.Default
    private Integer dataStatus = 0;
}
