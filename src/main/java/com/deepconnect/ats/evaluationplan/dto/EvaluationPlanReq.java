package com.deepconnect.ats.evaluationplan.dto;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRound;
import lombok.Data;

import java.util.List;

@Data
public class EvaluationPlanReq {
    private String id;
    private String name;
    private Integer workLocation;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private List<EvaluationPlanRound> rounds;
    private String ownerId;
}
