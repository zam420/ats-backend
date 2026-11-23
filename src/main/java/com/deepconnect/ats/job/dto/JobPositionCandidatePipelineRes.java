package com.deepconnect.ats.job.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import com.deepconnect.ats.job.model.JobPosition;

@Getter
@Setter
public class JobPositionCandidatePipelineRes {

    protected String id;
    protected String title;

    protected JobPositionCandidatePipelineRes(JobPosition entity) {
        this.id = entity.getPublicId();
        this.title = entity.getTitle();
    }
    
    public static JobPositionCandidatePipelineRes from(JobPosition entity) {
        return new JobPositionCandidatePipelineRes(entity);
    }

    public static List<JobPositionCandidatePipelineRes> from(List<JobPosition> entities) {
        return entities.stream().map(JobPositionCandidatePipelineRes::from).toList();
    }
}
