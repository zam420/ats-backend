package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;

@Data
public class ScreeningQuestionsFormDetailsRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private Boolean isSharedWithinTenant;
    private List<ScreeningQuestionRes> questions;
    private Set<String> sharedWithUserIds;
    private String ownerId;

    public static ScreeningQuestionsFormDetailsRes from(ScreeningQuestionsForm entity) {
        if(entity == null)
            return null;
        ScreeningQuestionsFormDetailsRes res = new ScreeningQuestionsFormDetailsRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
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

    public static List<ScreeningQuestionsFormDetailsRes> fromList(List<ScreeningQuestionsForm> entities) {
        return entities.stream().map(ScreeningQuestionsFormDetailsRes::from).toList();
    }
}
