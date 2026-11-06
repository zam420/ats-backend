package com.deepconnect.ats.job.dto;

import jakarta.persistence.Column;
import lombok.Data;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import jakarta.validation.constraints.NotNull;

import com.deepconnect.ats.job.model.JobPosition;

@Data
public class JobPositionReq {
    
    private String title;

    @Column(nullable = false, length = 10000, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 5000, columnDefinition = "TEXT")
    private String mandatoryRequirements;

    @Column(nullable = true, length = 5000, columnDefinition = "TEXT")
    private String preferredRequirements;

    @Column(name = "employmentType", nullable = false)
    @NotNull(message = "{field.job.employmentType}{validation.notBlank}")
    private Integer employmentType;

    @Column(name = "workArrangementType", nullable = false)
    @NotNull(message = "{field.job.workArrangementType}{validation.notBlank}")
    private Integer workArrangementType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "work_location", nullable = false, columnDefinition = "json")
    private List<Integer> workLocation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> recruiters;

    @Column(name = "recruitmentPipeline", nullable = false)
    private String recruitmentPipeline;

    @Column(name = "evaluationPlan", nullable = false)
    private String evaluationPlan;

    public JobPosition toEntity() {
        JobPosition position = new JobPosition();
        position.setTitle(this.getTitle());
        position.setDescription(this.getDescription());
        position.setMandatoryRequirements(this.getMandatoryRequirements());
        position.setPreferredRequirements(this.getPreferredRequirements());
        position.setEmploymentTypeId(this.getEmploymentType());
        position.setWorkArrangementTypeId(this.getWorkArrangementType());
        position.setWorkLocation(this.getWorkLocation());
        position.setRecruiters(this.getRecruiters());
        position.setRecruitmentPipelinePublicId(this.getRecruitmentPipeline());
        position.setEvaluationPlanPublicId(this.getEvaluationPlan());
        return position;
    }

}
