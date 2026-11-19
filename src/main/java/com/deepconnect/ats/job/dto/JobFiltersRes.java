package com.deepconnect.ats.job.dto;

import lombok.Data;
import com.deepconnect.ats.job.model.JobExperienceLevelType;

import java.util.List;

import com.deepconnect.ats.common.dto.EnumsRes;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRes;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanResMin;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormRes;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormResMin;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundType;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRes;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineResMin;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundType;
import com.deepconnect.ats.user.dto.UserSummary;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionType;
import com.deepconnect.ats.job.enums.JobQuestionsDisplayMode;
import com.deepconnect.ats.job.model.JobEmploymentType;
import com.deepconnect.ats.job.model.JobWorkArrangementType;

@Data
public class JobFiltersRes {

    private List<JobEmploymentType> employmentTypes;
    private List<JobWorkArrangementType> workArrangementTypes;
    private List<JobExperienceLevelType> experienceLevelTypes;

    private List<EnumsRes> jobStatus;
    private List<EnumsRes> jobQuestionsDisplayMode;

    private List<EvaluationPlanResMin> evaluationPlans;
    private List<RecruitmentPipelineResMin> recruitmentPipelines;
    private List<ScreeningQuestionsFormResMin> screeningQuestionsForms;
    private List<UserSummary> recruiters;
    private List<UserSummary> hiringManagers;
    private List<EnumsRes> jobPostedDateTypes;
    private List<EvaluationPlanRoundType> evaluationPlanRoundTypes;
    private List<RecruitmentPipelineRoundType> recruitmentPipelineRoundTypes;
    private List<ScreeningQuestionType> screeningQuestionTypes;
    
    public static JobFiltersRes from(
        List<JobEmploymentType> employmentTypes, 
        List<JobWorkArrangementType> workArrangementTypes, 
        List<JobExperienceLevelType> experienceLevelTypes,
        List<EnumsRes> jobStatus,
        List<EnumsRes> jobQuestionsDisplayMode,
        List<EnumsRes> jobPostedDateTypes,
        List<EvaluationPlanRes> evaluationPlans, 
        List<RecruitmentPipelineRes> recruitmentPipelines,
        List<ScreeningQuestionsFormRes> screeningQuestionsForms,
        List<UserSummary> recruiters,
        List<UserSummary> hiringManagers,
        List<EvaluationPlanRoundType> evaluationPlanRoundTypes,
        List<RecruitmentPipelineRoundType> recruitmentPipelineRoundTypes,
        List<ScreeningQuestionType> screeningQuestionTypes) 
    {
        JobFiltersRes res = new JobFiltersRes();
        res.setEmploymentTypes(employmentTypes);
        res.setWorkArrangementTypes(workArrangementTypes);
        res.setExperienceLevelTypes(experienceLevelTypes);
        res.setJobStatus(jobStatus);
        res.setJobQuestionsDisplayMode(jobQuestionsDisplayMode);
        res.setJobPostedDateTypes(jobPostedDateTypes);
        res.setEvaluationPlans(EvaluationPlanResMin.fromResList(evaluationPlans));
        res.setRecruitmentPipelines(RecruitmentPipelineResMin.fromResList(recruitmentPipelines));
        res.setScreeningQuestionsForms(ScreeningQuestionsFormResMin.fromResList(screeningQuestionsForms));
        res.setRecruiters(recruiters);
        res.setHiringManagers(hiringManagers);
        res.setEvaluationPlanRoundTypes(evaluationPlanRoundTypes);
        res.setRecruitmentPipelineRoundTypes(recruitmentPipelineRoundTypes);
        res.setScreeningQuestionTypes(screeningQuestionTypes);
        return res;
    }

}
