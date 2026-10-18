package com.deepconnect.ats.dto;

import lombok.Data;

import java.util.List;

@Data
public class InterviewSetupReq {
    private Long targetJobId;
    private Integer interviewType;
    private Long interviewTemplateId;
    private List<Long> candidateResumeIds;
}
