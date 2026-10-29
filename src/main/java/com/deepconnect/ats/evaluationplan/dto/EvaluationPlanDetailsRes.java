package com.deepconnect.ats.evaluationplan.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;

@Data
public class EvaluationPlanDetailsRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private List<EvaluationPlanRoundRes> rounds;
    private String ownerId;

    public static EvaluationPlanDetailsRes from(EvaluationPlan entity) {
        if(entity == null)
            return null;
        EvaluationPlanDetailsRes res = new EvaluationPlanDetailsRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        res.setRounds(
        entity.getRounds() == null
            ? List.of()
            : entity.getRounds().stream().map(EvaluationPlanRoundRes::from).toList()
    );
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<EvaluationPlanDetailsRes> fromList(List<EvaluationPlan> entities) {
        return entities.stream().map(EvaluationPlanDetailsRes::from).toList();
    }
}
