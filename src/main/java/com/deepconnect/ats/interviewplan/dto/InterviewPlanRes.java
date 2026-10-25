package com.deepconnect.ats.interviewplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.interviewplan.model.InterviewPlan;

@Data
public class InterviewPlanRes {
    private String id;
    private String name;
    private Integer workLocation;
    private String notes;
    private Integer status;

    public static InterviewPlanRes from(InterviewPlan entity) {
        InterviewPlanRes res = new InterviewPlanRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setWorkLocation(entity.getWorkLocation());
        res.setNotes(entity.getNotes());
        res.setStatus(entity.getStatus());
        return res;
    }

    public static List<InterviewPlanRes> fromList(List<InterviewPlan> entities) {
        return entities.stream().map(InterviewPlanRes::from).toList();
    }
}
