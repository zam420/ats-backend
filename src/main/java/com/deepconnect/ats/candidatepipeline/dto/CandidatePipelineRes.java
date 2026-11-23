package com.deepconnect.ats.candidatepipeline.dto;

import java.util.List;

import com.deepconnect.ats.candidate.dto.CandidateClosureRes;

import lombok.Data;

@Data 
public class CandidatePipelineRes {

    public String jobId;
    public String jobTitle;
    public long totalApplications;
    public long pipelineCount;
    
    public List<CandidatePipelineStageRes> stages;
    public List<CandidateClosureRes> rejectedCandidates;

    public static CandidatePipelineRes from(
        String jobId, 
        String jobTitle, 
        long totalApplications, 
        long pipelineCount,
        List<CandidatePipelineStageRes> stages, 
        List<CandidateClosureRes> rejectedCandidates
    ) {
        CandidatePipelineRes res = new CandidatePipelineRes();
        res.jobId = jobId;
        res.jobTitle = jobTitle;
        res.totalApplications = totalApplications;
        res.pipelineCount = pipelineCount;
        res.stages = stages;
        res.rejectedCandidates = rejectedCandidates;
        return res;
    }
    
}
