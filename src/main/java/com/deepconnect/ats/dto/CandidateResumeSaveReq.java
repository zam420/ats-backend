package com.deepconnect.ats.dto;

import com.deepconnect.ats.model.CandidateEducationHistory;
import com.deepconnect.ats.model.CandidateProjectExperience;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.CandidateWorkExperience;
import lombok.Data;

import java.util.List;

@Data
public class CandidateResumeSaveReq {
    private CandidateResume candidateResume;
    private List<CandidateWorkExperience> workExperiences;
    private List<CandidateEducationHistory> educationHistories;
    private List<CandidateProjectExperience> projectExperiences;
}
