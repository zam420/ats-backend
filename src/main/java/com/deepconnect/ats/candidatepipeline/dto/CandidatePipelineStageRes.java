package com.deepconnect.ats.candidatepipeline.dto;

import java.util.List;

import com.deepconnect.ats.candidate.dto.CandidateResBase;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;

import lombok.Data;

@Data 
public class CandidatePipelineStageRes {

    public String id;
    public String name;
    public String stageType;
    public int order;
    public List<CandidateResBase> candidates;

    public static CandidatePipelineStageRes from(JobRecruitmentPipelineRound recruitmentPipelineRound, List<CandidateResBase> candidates) {
        CandidatePipelineStageRes res = new CandidatePipelineStageRes();
        res.id = recruitmentPipelineRound.getPublicId();
        res.name = recruitmentPipelineRound.getName();
        res.stageType = recruitmentPipelineRound.getRoundType().name();
        res.order = recruitmentPipelineRound.getOrder();
        res.candidates = candidates;
        return res;
    }

}
