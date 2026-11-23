package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;

@Getter
@Setter
public class ScreeningQuestionsFormDetailsRes extends ScreeningQuestionsFormResBase {

    private List<ScreeningQuestionRes> questions;
    private Set<String> sharedWithUserIds;
    private String ownerId;

    protected ScreeningQuestionsFormDetailsRes(ScreeningQuestionsForm entity, String ownerName) {
        super(entity, ownerName);
    }

    public static ScreeningQuestionsFormDetailsRes from(ScreeningQuestionsForm entity, String ownerName) {
        if(entity == null)
            return null;
        ScreeningQuestionsFormDetailsRes res = new ScreeningQuestionsFormDetailsRes(entity, ownerName);
        res.setQuestions(
        entity.getQuestions() == null
            ? List.of()
            : entity.getQuestions().stream().map(ScreeningQuestionRes::from).toList()
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

    public static List<ScreeningQuestionsFormDetailsRes> from(List<ScreeningQuestionsForm> entities, HashMap<Long, String> ownerNamesById) {
        return entities.stream().map(e -> ScreeningQuestionsFormDetailsRes.from(e, ownerNamesById.get(e.getOwnerId()))).toList();
    }

}
