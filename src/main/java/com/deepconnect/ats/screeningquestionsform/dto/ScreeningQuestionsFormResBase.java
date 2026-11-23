package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Data;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;

@Data
public class ScreeningQuestionsFormResBase {

    protected String id;
    protected String name;
    protected String description;
    protected Boolean isPublic;
    protected Boolean isSystemTemplate;
    protected Boolean isSharedWithinTenant;
    protected String ownerId;
    protected String ownerName;

    protected ScreeningQuestionsFormResBase(ScreeningQuestionsForm entity, String ownerName) {
        this.id = entity.getPublicId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.isPublic = entity.getIsPublicForTenant();
        this.isSystemTemplate = entity.getIsPublicForAll();
        this.isSharedWithinTenant = entity.getIsSharedWithinTenant();
        this.ownerId = entity.getOwnerPublicId();
        this.ownerName = ownerName;
    }
    
    public static ScreeningQuestionsFormResBase from(ScreeningQuestionsForm entity, String ownerName) {
        return new ScreeningQuestionsFormResBase(entity, ownerName);
    }
    
}
