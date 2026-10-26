package com.deepconnect.ats.interviewplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.interviewplan.model.InterviewPlan;

@Data
public class InterviewPlanRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private String ownerId;
    
    public static InterviewPlanRes from(InterviewPlan entity) {
        InterviewPlanRes res = new InterviewPlanRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<InterviewPlanRes> fromList(List<InterviewPlan> entities) {
        return entities.stream().map(InterviewPlanRes::from).toList();
    }
}
