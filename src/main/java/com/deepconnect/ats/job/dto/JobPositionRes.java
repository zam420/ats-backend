package com.deepconnect.ats.job.dto;

import lombok.Data;

import java.util.List;
import com.deepconnect.ats.job.model.JobPosition;

@Data
public class JobPositionRes {

    private String id;
    private String title;
    private String description;
    private String mandatoryRequirements;
    private String preferredRequirements;
    private Integer employmentType;
    private Integer workArrangementType;
    private List<Integer> workLocation;
    private List<String> recruiters;
    private String recruitmentPipeline;
    private String evaluationPlan;
    
    public static JobPositionRes from(JobPosition entity) {
        JobPositionRes res = new JobPositionRes();
        res.setId(entity.getPublicId());
        res.setTitle(entity.getTitle());
        res.setDescription(entity.getDescription());
        res.setMandatoryRequirements(entity.getMandatoryRequirements());
        res.setPreferredRequirements(entity.getPreferredRequirements());
        res.setEmploymentType(entity.getEmploymentTypeId());
        res.setWorkArrangementType(entity.getWorkArrangementTypeId());
        res.setWorkLocation(entity.getWorkLocation());
        res.setRecruiters(entity.getRecruiters());
        res.setRecruitmentPipeline(entity.getRecruitmentPipelinePublicId());
        res.setEvaluationPlan(entity.getEvaluationPlanPublicId());
        return res;
    }

    public static List<JobPositionRes> fromList(List<JobPosition> entities) {
        return entities.stream().map(JobPositionRes::from).toList();
    }
}
