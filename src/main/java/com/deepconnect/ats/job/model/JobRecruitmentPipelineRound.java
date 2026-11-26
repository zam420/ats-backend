package com.deepconnect.ats.job.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRoundReqRes;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundBase;

@Entity
@Table(name = "job_recruitment_pipeline_round", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"recruitment_pipeline", "`order`"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobRecruitmentPipelineRound extends RecruitmentPipelineRoundBase {

    @JsonBackReference("recruitment_pipeline_rounds")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jobPosition", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private JobPosition jobPosition;

    @Column(name = "jobPosition", nullable = false)
    private Long jobPositionId;

    protected JobRecruitmentPipelineRound(RecruitmentPipelineRoundReqRes dto, Long jobPositionId) {
        super(dto);
        this.jobPositionId = jobPositionId;
    }

    public static JobRecruitmentPipelineRound from(RecruitmentPipelineRoundReqRes dto, Long jobPositionId) {
        if(dto == null)
            return null;
        return new JobRecruitmentPipelineRound(dto, jobPositionId);
    }

    public static List<JobRecruitmentPipelineRound> from(List<RecruitmentPipelineRoundReqRes> entities, Long jobPositionId) {
        return entities.stream().map(dto -> JobRecruitmentPipelineRound.from(dto, jobPositionId)).toList();
    }

}
