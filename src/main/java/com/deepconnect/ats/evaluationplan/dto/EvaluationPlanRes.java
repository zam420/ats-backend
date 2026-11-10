package com.deepconnect.ats.evaluationplan.dto;

import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRound;

@Data
public class EvaluationPlanRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private Boolean isSharedWithinTenant;
    private String ownerId;
    // Round names in order, joined as "name1 -> name2"; generated on the fly, never stored
    private String roundSequence;
    
    public static EvaluationPlanRes from(EvaluationPlan entity) {
        EvaluationPlanRes res = new EvaluationPlanRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        res.setIsSharedWithinTenant(entity.getIsSharedWithinTenant());
        res.setOwnerId(entity.getOwnerPublicId());
        if (entity.getRounds() != null) {
            res.setRoundSequence(entity.getRounds().stream()
                .sorted(Comparator.comparing(EvaluationPlanRound::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(EvaluationPlanRound::getName)
                .filter(n -> n != null && !n.isBlank())
                .collect(Collectors.joining(" -> ")));
        }
        return res;
    }

    public static List<EvaluationPlanRes> fromList(List<EvaluationPlan> entities) {
        return entities.stream().map(EvaluationPlanRes::from).toList();
    }
}
