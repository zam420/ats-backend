package com.deepconnect.ats.screeningquestiontemplate.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestionTemplate;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;

@Data
public class ScreeningQuestionTemplateDetailsRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private List<ScreeningQuestionRes> questions;
    private Set<String> sharedWithUserIds;
    private String ownerId;

    public static ScreeningQuestionTemplateDetailsRes from(ScreeningQuestionTemplate entity) {
        if(entity == null)
            return null;
        ScreeningQuestionTemplateDetailsRes res = new ScreeningQuestionTemplateDetailsRes();
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
        }
        res.setSharedWithUserIds(
        entity.getSharedWithUsers() == null
            ? Set.of()
            : entity.getSharedWithUsers().stream().map(u -> u.getUser().getPublicId()).collect(Collectors.toSet())
        );
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<ScreeningQuestionTemplateDetailsRes> fromList(List<ScreeningQuestionTemplate> entities) {
        return entities.stream().map(ScreeningQuestionTemplateDetailsRes::from).toList();
    }
}
