package com.deepconnect.ats.recruitmentpipeline.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRoundReqRes;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "recruitment_pipeline_round", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"recruitment_pipeline", "`order`"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RecruitmentPipelineRound extends RecruitmentPipelineRoundBase {

    @JsonBackReference("rounds")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentPipeline", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private RecruitmentPipeline recruitmentPipeline;

    @Column(name = "recruitmentPipeline", nullable = false)
    private Long recruitmentPipelineId;

    protected RecruitmentPipelineRound(RecruitmentPipelineRoundReqRes dto, Long recruitmentPipelineId) {
        super(dto);
        this.recruitmentPipelineId = recruitmentPipelineId;
    }

    public static RecruitmentPipelineRound from(RecruitmentPipelineRoundReqRes dto, Long recruitmentPipelineId) {
        if(dto == null)
            return null;
        return new RecruitmentPipelineRound(dto, recruitmentPipelineId);
    }

    public static List<RecruitmentPipelineRound> from(List<RecruitmentPipelineRoundReqRes> entities, Long recruitmentPipelineId) {
        return entities.stream().map(dto -> RecruitmentPipelineRound.from(dto, recruitmentPipelineId)).toList();
    }

}
