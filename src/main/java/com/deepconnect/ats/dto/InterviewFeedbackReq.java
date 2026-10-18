package com.deepconnect.ats.dto;

import lombok.Data;

import java.util.Map;

@Data
public class InterviewFeedbackReq {
    private Long id;
    private Long interviewCandidateId;
    private Long interviewRoundId;
    private Integer interviewResult;
    private Map<String, Object> roundSpecificFeedback;
}
