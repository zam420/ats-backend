package com.deepconnect.ats.job.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.deepconnect.ats.common.enums.JobStatus;
import com.deepconnect.ats.common.enums.JobWorkArrangement;
import com.deepconnect.ats.interviewplan.model.InterviewPlan;
import lombok.*;

import com.deepconnect.ats.common.enums.JobEmploymentType;
import com.deepconnect.ats.common.model.OperatableBaseEntity;
import com.deepconnect.ats.user.model.User;

@Entity
@Table(
    name = "job_position",
    indexes = {
        @Index(name = "idx_job_position_status", columnList = "status"),
        @Index(name = "idx_job_position_title", columnList = "title"),
        @Index(name = "idx_job_position_owner_id", columnList = "owner_id"),
        @Index(name = "idx_job_position_hiring_manager_id", columnList = "hiring_manager_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosition2 extends OperatableBaseEntity {

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 10000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 30)
    private JobEmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_arrangement", nullable = false, length = 30)
    private JobWorkArrangement workArrangement;

    @Size(max = 150)
    @Column(name = "work_location", length = 150)
    private String workLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private JobStatus status = JobStatus.DRAFT;

    /*
     * A job references an existing interview plan.
     * Do not cascade persistence or deletion to the shared plan.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_plan_id")
    private InterviewPlan interviewPlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hiring_manager_id")
    private User hiringManager;
}