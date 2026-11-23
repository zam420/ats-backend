package com.deepconnect.ats.evaluationplan.dto;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;

@Getter
@Setter 
public class EvaluationPlanDetailsRes extends EvaluationPlanResBase {
    private List<EvaluationPlanRoundRes> rounds;
    private Set<String> sharedWithUserIds;
    private String ownerId;

    protected EvaluationPlanDetailsRes(EvaluationPlan entity, String ownerName) {
        super(entity, ownerName);
    }

    public static EvaluationPlanDetailsRes from(EvaluationPlan entity, String ownerName) {
        if(entity == null)
            return null;
        EvaluationPlanDetailsRes res = new EvaluationPlanDetailsRes(entity, ownerName);
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
        if(!CollectionUtils.isEmpty(entity.getSharedWithUsers())){
            res.setIsPublic(false);
            res.setIsSharedWithinTenant(true);
        }
        res.setSharedWithUserIds(
        entity.getSharedWithUsers() == null
            ? Set.of()
            : entity.getSharedWithUsers().stream().map(u -> u.getUser().getPublicId()).collect(Collectors.toSet())
        );
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<EvaluationPlanDetailsRes> fromList(List<EvaluationPlan> entities, HashMap<Long, String> ownerNamesById) {
        return entities.stream().map(e -> EvaluationPlanDetailsRes.from(e, ownerNamesById.get(e.getOwnerId()))).toList();
    }
}
