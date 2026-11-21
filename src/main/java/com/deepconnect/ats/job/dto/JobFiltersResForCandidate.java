package com.deepconnect.ats.job.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.common.dto.EnumsRes;
import com.deepconnect.ats.common.dto.EnumsExtendedRes;

@Data
public class JobFiltersResForCandidate {
    private List<EnumsRes> employmentTypes;
    private List<EnumsRes> workArrangementTypes;
    private List<EnumsRes> experienceLevelTypes;
    private List<EnumsRes> jobStatus;
    private List<EnumsRes> jobPostedDateTypes;
    private List<EnumsExtendedRes> screeningQuestionTypes;
    
    public static JobFiltersResForCandidate from(
        List<EnumsRes> employmentTypes, 
        List<EnumsRes> workArrangementTypes, 
        List<EnumsRes> experienceLevelTypes,
        List<EnumsRes> jobStatus,
        List<EnumsRes> jobPostedDateTypes,
        List<EnumsExtendedRes> screeningQuestionTypes)
    {
        JobFiltersResForCandidate res = new JobFiltersResForCandidate();
        res.setEmploymentTypes(employmentTypes);
        res.setWorkArrangementTypes(workArrangementTypes);
        res.setExperienceLevelTypes(experienceLevelTypes);
        res.setJobStatus(jobStatus);
        res.setJobPostedDateTypes(jobPostedDateTypes);
        res.setScreeningQuestionTypes(screeningQuestionTypes);
        return res;
    }

}
