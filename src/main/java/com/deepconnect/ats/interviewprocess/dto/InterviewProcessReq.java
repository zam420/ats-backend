package com.deepconnect.ats.interviewprocess.dto;

import com.deepconnect.ats.interviewprocess.model.InterviewProcessRound;
import lombok.Data;

import java.util.List;

@Data
public class InterviewProcessReq {
    private String id;
    private String name;
    private Integer workLocation;
    private String notes;
    private Integer status;
    private List<InterviewProcessRound> rounds;
}
