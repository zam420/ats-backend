package com.deepconnect.ats.job.dto;

import lombok.Data;
import com.deepconnect.ats.job.model.JobExperienceLevelType;

import java.util.List;

import com.deepconnect.ats.common.dto.EnumsRes;
import com.deepconnect.ats.job.model.JobEmploymentType;
import com.deepconnect.ats.job.model.JobWorkArrangementType;
import com.deepconnect.ats.common.dto.EnumsExtendedRes;

@Data
public class JobFiltersResForCandidate {
    private List<JobEmploymentType> employmentTypes;
    private List<JobWorkArrangementType> workArrangementTypes;
    private List<JobExperienceLevelType> experienceLevelTypes;
    private List<EnumsRes> jobStatus;
    private List<EnumsRes> jobPostedDateTypes;
    private List<EnumsExtendedRes> screeningQuestionTypes;
    
    public static JobFiltersResForCandidate from(
        List<JobEmploymentType> employmentTypes, 
        List<JobWorkArrangementType> workArrangementTypes, 
        List<JobExperienceLevelType> experienceLevelTypes,
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
