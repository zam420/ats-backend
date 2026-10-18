package com.deepconnect.ats.dto;

import com.deepconnect.ats.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateResumeDetailRsp {

    private CandidateResume candidateResume;
    private Candidate candidate;
    private JobPosition position;
    private CandidateResumeOffer offer;

    @Builder.Default
    private List<CandidateWorkExperience> workExperiences = new ArrayList<>();

    @Builder.Default
    private List<CandidateEducationHistory> educationHistories = new ArrayList<>();

    @Builder.Default
    private List<CandidateProjectExperience> projectExperiences = new ArrayList<>();

    @Builder.Default
    private List<ResumeComment> comments = new ArrayList<>();

    @Builder.Default
    private List<ResumeTag> tags = new ArrayList<>();
}
