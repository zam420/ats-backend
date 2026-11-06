package com.deepconnect.ats.job.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private String title;

    @Column(nullable = false, length = 10000, columnDefinition = "TEXT")
    @JsonSerialize(using = JobRichTextSerializer.class)
    private String description;

    @Column(nullable = false, length = 5000, columnDefinition = "TEXT")
    @JsonSerialize(using = JobRichTextSerializer.class)
    private String mandatoryRequirements;

    @Column(nullable = true, length = 5000, columnDefinition = "TEXT")
    @JsonSerialize(using = JobRichTextSerializer.class)
    private String preferredRequirements;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "work_location", nullable = false, columnDefinition = "json")
    private List<Integer> workLocation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private List<String> recruiters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentPipelineId", referencedColumnName = "id", insertable = false, updatable = false)
    private RecruitmentPipeline recruitmentPipeline;

    @Column(name = "recruitmentPipelineId", nullable = false)
    private Long recruitmentPipelineId;

    @Column(name = "recruitmentPipelinePublicId", nullable = false)
    private String recruitmentPipelinePublicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluationPlanId", referencedColumnName = "id", insertable = false, updatable = false)
    private EvaluationPlan evaluationPlan;

    @Column(name = "evaluationPlanId", nullable = false)
    private Long evaluationPlanId;

    @Column(name = "evaluationPlanPublicId", nullable = false)
    private String evaluationPlanPublicId;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private JobStatus status = JobStatus.DRAFT;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extended_questions", columnDefinition = "json")
    private List<Map<String, Object>> extendedQuestions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", referencedColumnName = "id", insertable = false, updatable = false)
    private User owner;

    @Column(name = "ownerId", nullable = false)
    private Long ownerId;

    @Column(name = "ownerPublicId", nullable = false)
    private String ownerPublicId;
}
