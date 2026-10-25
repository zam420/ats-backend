package com.deepconnect.ats.interviewplan.dto;

import com.deepconnect.ats.interviewplan.model.InterviewPlanRound;
import lombok.Data;

import java.util.List;

@Data
public class InterviewPlanReq {
    private String id;
    private String name;
    private Integer workLocation;
    private String notes;
    private Integer status;
    private List<InterviewPlanRound> rounds;
}
