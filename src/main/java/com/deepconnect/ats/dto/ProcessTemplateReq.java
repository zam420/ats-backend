package com.deepconnect.ats.dto;

import com.deepconnect.ats.model.InterviewRoundTemplate;
import lombok.Data;

import java.util.List;

@Data
public class ProcessTemplateReq {
    private Long id;
    private String name;
    private Integer workLocation;
    private String notes;
    private Integer status;
    private List<InterviewRoundTemplate> rounds;
}
