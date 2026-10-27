package com.deepconnect.ats.interviewplan.dto;

import com.deepconnect.ats.interviewplan.model.InterviewPlanRound;
import lombok.Data;

import java.util.List;

@Data
public class InterviewPlanReq {
    private String id;
    private String name;
    private Integer workLocation;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private List<InterviewPlanRound> rounds;
    private String ownerId;
}
