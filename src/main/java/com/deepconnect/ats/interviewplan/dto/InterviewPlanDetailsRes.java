package com.deepconnect.ats.interviewplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.interviewplan.model.InterviewPlan;

@Data
public class InterviewPlanDetailsRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private List<InterviewPlanRoundRes> rounds;
    private String ownerId;

    public static InterviewPlanDetailsRes from(InterviewPlan entity) {
        if(entity == null)
            return null;
        InterviewPlanDetailsRes res = new InterviewPlanDetailsRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        res.setRounds(
        entity.getRounds() == null
            ? List.of()
            : entity.getRounds().stream().map(InterviewPlanRoundRes::from).toList()
    );
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<InterviewPlanDetailsRes> fromList(List<InterviewPlan> entities) {
        return entities.stream().map(InterviewPlanDetailsRes::from).toList();
    }
}
