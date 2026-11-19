package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Data;
import java.util.List;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;

@Data
public class ScreeningQuestionsFormRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private Boolean isSharedWithinTenant;
    private String ownerId;
    
    public static ScreeningQuestionsFormRes from(ScreeningQuestionsForm entity) {
        ScreeningQuestionsFormRes res = new ScreeningQuestionsFormRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        res.setIsSharedWithinTenant(entity.getIsSharedWithinTenant());
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<ScreeningQuestionsFormRes> fromList(List<ScreeningQuestionsForm> entities) {
        return entities.stream().map(ScreeningQuestionsFormRes::from).toList();
    }
}
