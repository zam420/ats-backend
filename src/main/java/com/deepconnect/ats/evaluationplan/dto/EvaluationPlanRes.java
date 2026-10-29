package com.deepconnect.ats.evaluationplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;

@Data
public class EvaluationPlanRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private String ownerId;
    
    public static EvaluationPlanRes from(EvaluationPlan entity) {
        EvaluationPlanRes res = new EvaluationPlanRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<EvaluationPlanRes> fromList(List<EvaluationPlan> entities) {
        return entities.stream().map(EvaluationPlanRes::from).toList();
    }
}
