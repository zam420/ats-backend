package com.deepconnect.ats.job.dto;

import lombok.Data;

import java.util.List;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRes;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRes;
import com.deepconnect.ats.user.dto.UserSummary;

import com.deepconnect.ats.job.model.JobEmploymentType;
import com.deepconnect.ats.job.model.JobWorkArrangementType;

@Data
public class JobFieldsData {
    private List<JobEmploymentType> employmentTypes;
    private List<JobWorkArrangementType> workArrangementTypes;
    private List<EvaluationPlanRes> evaluationPlans;
    private List<RecruitmentPipelineRes> recruitmentPipelines;
    private List<UserSummary> recruiters;
    private List<UserSummary> hiringManagers;
    
    public static JobFieldsData from(
        List<JobEmploymentType> employmentTypes, 
        List<JobWorkArrangementType> workArrangementTypes, 
        List<EvaluationPlanRes> evaluationPlans, 
        List<RecruitmentPipelineRes> recruitmentPipelines,
        List<UserSummary> recruiters,
        List<UserSummary> hiringManagers) 
    {
        JobFieldsData res = new JobFieldsData();
        res.setEmploymentTypes(employmentTypes);
        res.setWorkArrangementTypes(workArrangementTypes);
        res.setEvaluationPlans(evaluationPlans);
        res.setRecruitmentPipelines(recruitmentPipelines);
        res.setRecruiters(recruiters);
        res.setHiringManagers(hiringManagers);
        return res;
    }

}
