package com.deepconnect.ats.interviewplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.interviewplan.model.InterviewPlan;

@Data
public class InterviewPlanDetailsRes {
    private String id;
    private String name;
    private Integer workLocation;
    private String notes;
    private Integer status;
    private List<InterviewPlanRoundRes> rounds;

    public static InterviewPlanDetailsRes from(InterviewPlan entity) {
        if(entity == null)
            return null;
        InterviewPlanDetailsRes res = new InterviewPlanDetailsRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setWorkLocation(entity.getWorkLocation());
        res.setNotes(entity.getNotes());
        res.setStatus(entity.getStatus());
        res.setRounds(
        entity.getRounds() == null
            ? List.of()
            : entity.getRounds().stream().map(InterviewPlanRoundRes::from).toList()
    );
        return res;
    }

    public static List<InterviewPlanDetailsRes> fromList(List<InterviewPlan> entities) {
        return entities.stream().map(InterviewPlanDetailsRes::from).toList();
    }
}
