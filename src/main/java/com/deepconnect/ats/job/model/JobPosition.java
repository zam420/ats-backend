package com.deepconnect.ats.job.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.List;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.common.model.OperatableBaseWithTenantEntity;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.security.JobRichTextSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "job_position", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "project_id"})
    },
    indexes = {
        @Index(name = "idx_job_position_status", columnList = "status"),
        @Index(name = "idx_job_position_title", columnList = "title"),
        @Index(name = "idx_job_position_owner_id", columnList = "owner_id"),
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosition extends OperatableBaseWithTenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "{field.job.title}{validation.notBlank}")
    @Length(max = 100, message = "{field.job.title}{validation.lengthMax}")
    private String title;

    @Column(nullable = false, length = 10000, columnDefinition = "TEXT")
    @JsonSerialize(using = JobRichTextSerializer.class)
    @NotBlank(message = "{field.job.description}{validation.notBlank}")
    @Length(max = 10000, message = "{field.job.description}{validation.lengthMax}")
    private String description;

    // @Column(nullable = false, length = 5000, columnDefinition = "TEXT")
    // @JsonSerialize(using = JobRichTextSerializer.class)
    // @NotBlank(message = "{field.job.mandatoryRequirements}{validation.notBlank}")
    // @Length(max = 5000, message = "{field.job.mandatoryRequirements}{validation.lengthMax}")
    // private String mandatoryRequirements;

    // @Column(nullable = true, length = 5000, columnDefinition = "TEXT")
    // @JsonSerialize(using = JobRichTextSerializer.class)
    // @Length(max = 5000, message = "{field.job.preferredRequirements}{validation.lengthMax}")
    // private String preferredRequirements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employmentTypeId", referencedColumnName = "id", insertable = false, updatable = false)
    private JobEmploymentType employmentType; // By default, the user who created this record. Can be changed.

    @Column(name = "employmentTypeId", nullable = false)
    @NotNull(message = "{field.job.employmentType}{validation.notBlank}")
    private Integer employmentTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workArrangementTypeId", referencedColumnName = "id", insertable = false, updatable = false)
    private JobWorkArrangementType workArrangementType; // By default, the user who created this record. Can be changed.

    @Column(name = "workArrangementTypeId", nullable = false)
    @NotNull(message = "{field.job.workArrangementType}{validation.notBlank}")
    private Integer workArrangementTypeId;

    //TODO: Later map it to Locations entity in DB (At present, we are using fixed ENUM)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "work_location", nullable = false, columnDefinition = "json")
    @NotEmpty(message = "{field.job.workLocation}{validation.notEmpty}")
    private List<Integer> workLocation;


    /* CONFIGURATION FIELDS */

    // FOR INTERNAL JOB CONFIGURATION

    @Column(nullable = false, length = 20)
    @NotBlank(message = "{field.job.requisitionId}{validation.notBlank}")
    @Length(min=4, max = 20, message = "{field.job.requisitionId}{validation.length}")
    private String requisitionId;

    @Column(name = "numOfOpenings", nullable = false, columnDefinition = "int default 1")
    @NotNull(message = "{field.job.numOfOpenings}{validation.notBlank}")
    @Range(min = 1, max = 50, message = "{field.job.numOfOpenings}{validation.range}")
    @Builder.Default
    private Integer numOfOpenings = 1;

    //TODO: Later map it to Office entity in DB
    @Column(nullable = false, length = 100)
    @NotBlank(message = "{field.job.office}{validation.notBlank}")
    @Length(min=4, max = 100, message = "{field.job.office}{validation.length}")
    private String office;

    //TODO: Later map it to Department entity in DB
    @Column(nullable = false, length = 100)
    @NotBlank(message = "{field.job.department}{validation.notBlank}")
    @Length(min=4, max = 100, message = "{field.job.department}{validation.length}")
    private String department;


    // FOR RECRUITERS AND HIRING MANAGERS

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    @NotEmpty(message = "{field.job.recruiters}{validation.notEmpty}")
    private List<String> recruiters;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    @NotEmpty(message = "{field.job.hiringManagers}{validation.notEmpty}")
    private List<String> hiringManagers;


    /* RECRUITMENT PIPELINE FIELDS */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentPipelineId", referencedColumnName = "id", insertable = false, updatable = false)
    private RecruitmentPipeline recruitmentPipeline;

    @Column(name = "recruitmentPipelineId", nullable = false)
    @NotNull(message = "{field.job.recruitmentPipeline}{validation.notBlank}")
    private Long recruitmentPipelineId;

    @Column(name = "recruitmentPipelinePublicId", nullable = false)
    private String recruitmentPipelinePublicId;

    /* EVALUATION PLAN FIELDS */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluationPlanId", referencedColumnName = "id", insertable = false, updatable = false)
    private EvaluationPlan evaluationPlan;

    @Column(name = "evaluationPlanId", nullable = false)
    @NotNull(message = "{field.job.evaluationPlan}{validation.notBlank}")
    private Long evaluationPlanId;

    @Column(name = "evaluationPlanPublicId", nullable = false)
    private String evaluationPlanPublicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private JobStatus status = JobStatus.DRAFT;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean deactivated = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publishedByUserId", referencedColumnName = "id", insertable = false, updatable = false)
    private User publishedByUser; // The user who published this record

    @Column(name = "publishedByUserId", nullable = true)
    private Long publishedByUserId;

    @Column(name = "publishedByUserPublicId", nullable = true)
    private String publishedByUserPublicId;

    @UpdateTimestamp
    @Column(name = "publishedAt", nullable = true)
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", referencedColumnName = "id", insertable = false, updatable = false)
    private User owner;

    @Column(name = "ownerId", nullable = false)
    private Long ownerId;

    @Column(name = "ownerPublicId", nullable = false)
    private String ownerPublicId;
}
