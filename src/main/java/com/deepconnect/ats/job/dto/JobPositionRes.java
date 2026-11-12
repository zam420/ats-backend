package com.deepconnect.ats.job.dto;

import lombok.Data;

import java.util.List;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.security.JobRichTextSanitizer;
import com.deepconnect.ats.job.enums.JobStatus;

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
    private String requisitionId;
    private Integer numOfOpenings;
    private String office;
    private String department;
    private List<String> recruiters;
    private List<String> hiringManagers;
    private String recruitmentPipeline;
    private String evaluationPlan;
    private JobStatus status;
    private String ownerId;
    
    public static JobPositionRes from(JobPosition entity) {
        JobPositionRes res = new JobPositionRes();
        res.setId(entity.getPublicId());
        res.setTitle(entity.getTitle());
        res.setDescription(JobRichTextSanitizer.sanitize(entity.getDescription()));
        // res.setMandatoryRequirements(JobRichTextSanitizer.sanitize(entity.getMandatoryRequirements()));
        // res.setPreferredRequirements(JobRichTextSanitizer.sanitize(entity.getPreferredRequirements()));
        res.setEmploymentType(entity.getEmploymentTypeId());
        res.setWorkArrangementType(entity.getWorkArrangementTypeId());
        res.setWorkLocation(entity.getWorkLocation());
        res.setRequisitionId(entity.getRequisitionId());
        res.setNumOfOpenings(entity.getNumOfOpenings());
        res.setOffice(entity.getOffice());
        res.setDepartment(entity.getDepartment());
        res.setRecruiters(entity.getRecruiters());
        res.setHiringManagers(entity.getHiringManagers());
        res.setRecruitmentPipeline(entity.getRecruitmentPipelinePublicId());
        res.setEvaluationPlan(entity.getEvaluationPlanPublicId());
        res.setStatus(entity.getStatus());
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<JobPositionRes> fromList(List<JobPosition> entities) {
        return entities.stream().map(JobPositionRes::from).toList();
    }
}
