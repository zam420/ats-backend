package com.deepconnect.ats.job.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.common.dto.EnumsRes;
import com.deepconnect.ats.common.dto.EnumsExtendedRes;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRes;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanResMin;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormRes;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormResMin;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRes;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineResMin;
import com.deepconnect.ats.user.dto.UserSummary;


@Data
public class JobFiltersRes {

    private List<EnumsRes> employmentTypes;
    private List<EnumsRes> workArrangementTypes;
    private List<EnumsRes> experienceLevelTypes;

    private List<EnumsRes> jobStatus;
    private List<EnumsRes> jobQuestionsDisplayMode;

    private List<EvaluationPlanResMin> evaluationPlans;
    private List<RecruitmentPipelineResMin> recruitmentPipelines;
    private List<ScreeningQuestionsFormResMin> screeningQuestionsForms;
    private List<UserSummary> recruiters;
    private List<UserSummary> hiringManagers;
    private List<EnumsRes> jobPostedDateTypes;
    private List<EnumsExtendedRes> evaluationPlanRoundTypes;
    private List<EnumsExtendedRes> recruitmentPipelineRoundTypes;
    private List<EnumsExtendedRes> screeningQuestionTypes;
    
    public static JobFiltersRes from(
        List<EnumsRes> employmentTypes, 
        List<EnumsRes> workArrangementTypes, 
        List<EnumsRes> experienceLevelTypes,
        List<EnumsRes> jobStatus,
        List<EnumsRes> jobQuestionsDisplayMode,
        List<EnumsRes> jobPostedDateTypes,
        List<EvaluationPlanRes> evaluationPlans, 
        List<RecruitmentPipelineRes> recruitmentPipelines,
        List<ScreeningQuestionsFormRes> screeningQuestionsForms,
        List<UserSummary> recruiters,
        List<UserSummary> hiringManagers,
        List<EnumsExtendedRes> evaluationPlanRoundTypes,
        List<EnumsExtendedRes> recruitmentPipelineRoundTypes,
        List<EnumsExtendedRes> screeningQuestionTypes) 
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
