package com.deepconnect.ats.recruitmentpipeline.dto;

import lombok.Data;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;

@Data
public class RecruitmentPipelineResBase {

    protected String id;
    protected String name;
    protected String description;
    protected Boolean isPublic;
    protected Boolean isSystemTemplate;
    protected Boolean isSharedWithinTenant;
    protected String ownerId;
    protected String ownerName;

    protected RecruitmentPipelineResBase(RecruitmentPipeline entity) {
        this.id = entity.getPublicId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.isPublic = entity.getIsPublicForTenant();
        this.isSystemTemplate = entity.getIsPublicForAll();
        this.isSharedWithinTenant = entity.getIsSharedWithinTenant();
        this.ownerId = entity.getOwnerPublicId();
        this.ownerName = entity.getOwnerName();
    }
    
    public static RecruitmentPipelineResBase from(RecruitmentPipeline entity) {
        return new RecruitmentPipelineResBase(entity);
    }

}
