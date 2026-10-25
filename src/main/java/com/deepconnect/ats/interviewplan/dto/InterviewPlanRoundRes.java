package com.deepconnect.ats.interviewplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.interviewplan.model.InterviewPlanRound;

@Data
public class InterviewPlanRoundRes {
    private String id;
    private String roundType;
    private List<String> interviewers;
    private Integer order;

    public static InterviewPlanRoundRes from(InterviewPlanRound entity) {
        if(entity == null)
            return null;
        InterviewPlanRoundRes res = new InterviewPlanRoundRes();
        res.setId(entity.getPublicId());
        res.setRoundType(entity.getRoundType().toString());
        res.setInterviewers(entity.getInterviewers());
        res.setOrder(entity.getOrder());
        return res;
    }

    public static List<InterviewPlanRoundRes> fromList(List<InterviewPlanRound> entities) {
        return entities.stream().map(InterviewPlanRoundRes::from).toList();
    }
}
