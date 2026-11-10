package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;

@Data
public class ScreeningQuestionsFormResMin {
    private String id;
    private String name;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    
    public static ScreeningQuestionsFormResMin from(ScreeningQuestionsForm entity) {
        ScreeningQuestionsFormResMin res = new ScreeningQuestionsFormResMin();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        return res;
    }

    public static ScreeningQuestionsFormResMin from(ScreeningQuestionsFormRes entity) {
        ScreeningQuestionsFormResMin res = new ScreeningQuestionsFormResMin();
        res.setId(entity.getId());
        res.setName(entity.getName());
        res.setIsPublic(entity.getIsPublic());
        res.setIsSystemTemplate(entity.getIsSystemTemplate());
        return res;
    }

    public static List<ScreeningQuestionsFormResMin> fromList(List<ScreeningQuestionsForm> entities) {
        return entities.stream().map(ScreeningQuestionsFormResMin::from).toList();
    }

    public static List<ScreeningQuestionsFormResMin> fromResList(List<ScreeningQuestionsFormRes> entities) {
        return entities.stream().map(ScreeningQuestionsFormResMin::from).toList();
    }
}
