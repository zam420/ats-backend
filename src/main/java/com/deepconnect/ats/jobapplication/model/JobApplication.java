package com.deepconnect.ats.jobapplication.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import com.deepconnect.ats.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    /*The ID of the recruitment pipeline stage the application is currently in.
    It can be null when a candidate was rejected in a custom stage, and later that stage was deleted from the job by job owner or hra.
    */
    @Column(name = "recruitment_pipeline_stage")
    private Long recruitmentPipelineStage;

    @Column(nullable = false)
    private boolean rejected;

    @Column(name = "rejection_reason", length = 2000)
    @Length(max = 2000, message = "{field.jobApplication.rejectionReason}{validation.lengthMax}")
    private String rejectionReason;

    @Column(name = "consider_for_other_openings")
    private Boolean considerForOtherOpenings;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejected_stage_name", length = 30)
    @Length(max = 30, message = "{field.jobApplication.rejectedStageName}{validation.lengthMax}")
    private String rejectedStageName;

    @Column(name = "rejected_stage_type", length = 30)
    @Length(max = 30, message = "{field.jobApplication.rejectedStageType}{validation.lengthMax}")
    private String rejectedStageType;

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
