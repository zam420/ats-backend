package com.deepconnect.ats.evaluationplan.dto;

import lombok.Data;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;

@Data
public class EvaluationPlanResBase {

    protected String id;
    protected String name;
    protected String description;
    protected Boolean isPublic;
    protected Boolean isSystemTemplate;
    protected Boolean isSharedWithinTenant;
    protected String ownerId;
    protected String ownerName; 

    protected EvaluationPlanResBase(EvaluationPlan entity, String ownerName) {
        this.id = entity.getPublicId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.isPublic = entity.getIsPublicForTenant();
        this.isSystemTemplate = entity.getIsPublicForAll();
        this.isSharedWithinTenant = entity.getIsSharedWithinTenant();
        this.ownerId = entity.getOwnerPublicId();
        this.ownerName = ownerName;
    }
    
    public static EvaluationPlanResBase from(EvaluationPlan entity, String ownerName) {
        return new EvaluationPlanResBase(entity, ownerName);
    }

}
