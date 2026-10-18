package com.deepconnect.ats.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class InterviewSubmitReq {
    private Long interviewId;
    private Long roundId;
    private Long candidateResumeId;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
    private Boolean isOnline;
    private String interviewLocation;
    private List<Long> interviewers;
    private Map<String, Object> teamsMeeting;
    private Long emailTemplateId;
}
