package com.deepconnect.ats.evaluationplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;

@Data
public class EvaluationPlanResMin {
    private String id;
    private String name;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    
    public static EvaluationPlanResMin from(EvaluationPlan entity) {
        EvaluationPlanResMin res = new EvaluationPlanResMin();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        return res;
    }

    public static EvaluationPlanResMin from(EvaluationPlanRes entity) {
        EvaluationPlanResMin res = new EvaluationPlanResMin();
        res.setId(entity.getId());
        res.setName(entity.getName());
        res.setIsPublic(entity.getIsPublic());
        res.setIsSystemTemplate(entity.getIsSystemTemplate());
        return res;
    }

    public static List<EvaluationPlanResMin> fromList(List<EvaluationPlan> entities) {
        return entities.stream().map(EvaluationPlanResMin::from).toList();
    }

    public static List<EvaluationPlanResMin> fromResList(List<EvaluationPlanRes> entities) {
        return entities.stream().map(EvaluationPlanResMin::from).toList();
    }
}
