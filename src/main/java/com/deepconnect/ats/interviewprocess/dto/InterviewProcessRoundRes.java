package com.deepconnect.ats.interviewprocess.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.interviewprocess.model.InterviewProcessRound;

@Data
public class InterviewProcessRoundRes {
    private String id;
    private String roundType;
    private List<String> interviewers;
    private Integer order;

    public static InterviewProcessRoundRes from(InterviewProcessRound entity) {
        if(entity == null)
            return null;
        InterviewProcessRoundRes res = new InterviewProcessRoundRes();
        res.setId(entity.getPublicId());
        res.setRoundType(entity.getRoundType().toString());
        res.setInterviewers(entity.getInterviewers());
        res.setOrder(entity.getOrder());
        return res;
    }

    public static List<InterviewProcessRoundRes> fromList(List<InterviewProcessRound> entities) {
        return entities.stream().map(InterviewProcessRoundRes::from).toList();
    }
}
