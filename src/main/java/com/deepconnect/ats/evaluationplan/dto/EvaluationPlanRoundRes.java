package com.deepconnect.ats.evaluationplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRound;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;

@Data
public class EvaluationPlanRoundRes {
    private String id;
    private String name;
    private String description;
    private EvaluationPlanRoundType roundType;
    private List<String> interviewers;
    private Integer order;

    public static EvaluationPlanRoundRes from(EvaluationPlanRound entity) {
        if(entity == null)
            return null;
        EvaluationPlanRoundRes res = new EvaluationPlanRoundRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setRoundType(entity.getRoundType());
        res.setInterviewers(entity.getInterviewers());
        res.setOrder(entity.getOrder());
        return res;
    }

    public static List<EvaluationPlanRoundRes> fromList(List<EvaluationPlanRound> entities) {
        return entities.stream().map(EvaluationPlanRoundRes::from).toList();
    }
}
