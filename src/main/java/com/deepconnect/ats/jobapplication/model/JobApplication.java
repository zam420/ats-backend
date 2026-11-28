package com.deepconnect.ats.jobapplication.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import com.deepconnect.ats.common.model.BaseEntity;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.jobapplication.enums.JobApplicationCloseType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "job_application", uniqueConstraints = @UniqueConstraint(columnNames = {"job_position_id", "email"}))
@Getter
@Setter
@NoArgsConstructor
public class JobApplication extends BaseEntity {

    @Column(name = "job_position_id", nullable = false)
    @NotNull(message = "{field.jobApplication.jobPosition}{validation.notBlank}")
    private Long jobPositionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_position_id", insertable = false, updatable = false)
    private JobPosition jobPosition;

    @Column(name = "job_position_public_id", nullable = false, length = 12, updatable = false)
    private String jobPositionPublicId; // Public URL identifier

    /*The ID of the recruitment pipeline stage the application is currently in.
    It can be null when a closed application's stage was later deleted.
    */
    @Column(name = "recruitment_pipeline_stage")
    private Long recruitmentPipelineStage;

    @Column(name = "evaluations_initialized", nullable = false)
    private boolean evaluationsInitialized;

    @Column(name = "evaluation_plan_version")
    private Integer evaluationPlanVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_decision", length = 30)
    private CandidateEvaluationDecision evaluationDecision;

    @Column(nullable = false)
    private boolean closed;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_close_type", length = 30)
    private JobApplicationCloseType applicationCloseType;

    @Column(name = "closed_reason", length = 2000)
    @Length(max = 2000, message = "{field.jobApplication.closedReason}{validation.lengthMax}")
    private String closedReason;

    @Column(name = "consider_for_other_openings")
    private Boolean considerForOtherOpenings;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "closed_at_stage_name", length = 30)
    @Length(max = 30, message = "{field.jobApplication.closedAtStageName}{validation.lengthMax}")
    private String closedAtStageName;

    @Column(name = "closed_at_stage_type", length = 30)
    @Length(max = 30, message = "{field.jobApplication.closedAtStageType}{validation.lengthMax}")
    private String closedAtStageType;

    @Column(name = "candidate_user_id", nullable = false)
    @NotNull(message = "{field.jobApplication.candidate}{validation.notBlank}")
    private Long candidateUserId;

    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "{field.jobApplication.firstName}{validation.notBlank}")
    @Length(max = 50, message = "{field.jobApplication.firstName}{validation.lengthMax}")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "{field.jobApplication.lastName}{validation.notBlank}")
    @Length(max = 50, message = "{field.jobApplication.lastName}{validation.lengthMax}")
    private String lastName;

    @Column(name = "preferred_name", length = 50)
    @Length(max = 50, message = "{field.jobApplication.preferredName}{validation.lengthMax}")
    private String preferredName;

    @JsonIgnore
    public String getName() {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }

    @Column(nullable = false, length = 100)
    @NotBlank(message = "{field.jobApplication.email}{validation.notBlank}")
    @Email(message = "{field.jobApplication.email}{validation.email}")
    @Length(min = 7, max = 100, message = "{field.jobApplication.email}{validation.length}")
    private String email;

    @Column(name = "country_code", nullable = false, length = 10)
    @NotBlank(message = "{field.jobApplication.countryCode}{validation.notBlank}")
    @Length(max = 10, message = "{field.jobApplication.countryCode}{validation.lengthMax}")
    private String countryCode;

    @Column(name = "phone_number", nullable = false, length = 40)
    @NotBlank(message = "{field.jobApplication.phoneNumber}{validation.notBlank}")
    @Length(max = 40, message = "{field.jobApplication.phoneNumber}{validation.lengthMax}")
    private String phoneNumber;

    @Column(name = "current_location", nullable = false, length = 200)
    @NotBlank(message = "{field.jobApplication.currentLocation}{validation.notBlank}")
    @Length(max = 200, message = "{field.jobApplication.currentLocation}{validation.lengthMax}")
    private String currentLocation;

    @Column(name = "resume_path", nullable = false, length = 255)
    @NotBlank(message = "{field.jobApplication.resumePath}{validation.notBlank}")
    @Length(max = 255, message = "{field.jobApplication.resumePath}{validation.lengthMax}")
    private String resumePath;

    @Column(name = "resume_name", nullable = false, length = 255)
    @NotBlank(message = "{field.jobApplication.resumeName}{validation.notBlank}")
    @Length(max = 255, message = "{field.jobApplication.resumeName}{validation.lengthMax}")
    private String resumeName;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
}
