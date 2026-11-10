package com.deepconnect.ats.job.dto;

import lombok.Data;

import java.util.List;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRes;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanResMin;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundType;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRes;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineResMin;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundType;
import com.deepconnect.ats.user.dto.UserSummary;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionType;

import com.deepconnect.ats.job.model.JobEmploymentType;
import com.deepconnect.ats.job.model.JobWorkArrangementType;

@Data
public class JobFieldsData {
    private List<JobEmploymentType> employmentTypes;
    private List<JobWorkArrangementType> workArrangementTypes;
    private List<EvaluationPlanResMin> evaluationPlans;
    private List<RecruitmentPipelineResMin> recruitmentPipelines;
    private List<UserSummary> recruiters;
    private List<UserSummary> hiringManagers;
    private List<EvaluationPlanRoundType> evaluationPlanRoundTypes;
    private List<RecruitmentPipelineRoundType> recruitmentPipelineRoundTypes;
    private List<ScreeningQuestionType> screeningQuestionTypes;
    
    public static JobFieldsData from(
        List<JobEmploymentType> employmentTypes, 
        List<JobWorkArrangementType> workArrangementTypes, 
        List<EvaluationPlanRes> evaluationPlans, 
        List<RecruitmentPipelineRes> recruitmentPipelines,
        List<UserSummary> recruiters,
        List<UserSummary> hiringManagers,
        List<EvaluationPlanRoundType> evaluationPlanRoundTypes,
        List<RecruitmentPipelineRoundType> recruitmentPipelineRoundTypes,
        List<ScreeningQuestionType> screeningQuestionTypes) 
    {
        JobFieldsData res = new JobFieldsData();
        res.setEmploymentTypes(employmentTypes);
        res.setWorkArrangementTypes(workArrangementTypes);
        res.setEvaluationPlans(EvaluationPlanResMin.fromResList(evaluationPlans));
        res.setRecruitmentPipelines(RecruitmentPipelineResMin.fromResList(recruitmentPipelines));
        res.setRecruiters(recruiters);
        res.setHiringManagers(hiringManagers);
        res.setEvaluationPlanRoundTypes(evaluationPlanRoundTypes);
        res.setRecruitmentPipelineRoundTypes(recruitmentPipelineRoundTypes);
        res.setScreeningQuestionTypes(screeningQuestionTypes);
        return res;
    }

}
