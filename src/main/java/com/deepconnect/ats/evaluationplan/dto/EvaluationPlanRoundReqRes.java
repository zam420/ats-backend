package com.deepconnect.ats.evaluationplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundBase;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;

@Data
public class EvaluationPlanRoundReqRes {

    private String id;
    private String name;
    private String description;
    private EvaluationPlanRoundType roundType;
    private List<String> interviewers;
    private Integer order;

    public static EvaluationPlanRoundReqRes from(EvaluationPlanRoundBase entity) {
        if(entity == null)
            return null;
        EvaluationPlanRoundReqRes res = new EvaluationPlanRoundReqRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setRoundType(entity.getRoundType());
        res.setInterviewers(entity.getInterviewers());
        res.setOrder(entity.getOrder());
        return res;
    }

    public static List<EvaluationPlanRoundReqRes> from(List<EvaluationPlanRoundBase> entities) {
        return entities.stream().map(EvaluationPlanRoundReqRes::from).toList();
    }

}
