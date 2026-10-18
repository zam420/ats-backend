package com.deepconnect.ats.service.resume;

import com.deepconnect.ats.model.CandidateEducationHistory;
import com.deepconnect.ats.model.CandidateProjectExperience;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.CandidateWorkExperience;

import java.util.List;

public record CandidateResumeAggregate(CandidateResume resume,
                                       List<CandidateEducationHistory> eduHistories,
                                       List<CandidateWorkExperience> workHistories,
                                       List<CandidateProjectExperience> projectHistories) {
}
