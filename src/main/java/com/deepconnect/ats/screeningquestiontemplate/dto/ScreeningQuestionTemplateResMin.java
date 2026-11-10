package com.deepconnect.ats.screeningquestiontemplate.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestionTemplate;

@Data
public class ScreeningQuestionTemplateResMin {
    private String id;
    private String name;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    
    public static ScreeningQuestionTemplateResMin from(ScreeningQuestionTemplate entity) {
        ScreeningQuestionTemplateResMin res = new ScreeningQuestionTemplateResMin();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        return res;
    }

    public static ScreeningQuestionTemplateResMin from(ScreeningQuestionTemplateRes entity) {
        ScreeningQuestionTemplateResMin res = new ScreeningQuestionTemplateResMin();
        res.setId(entity.getId());
        res.setName(entity.getName());
        res.setIsPublic(entity.getIsPublic());
        res.setIsSystemTemplate(entity.getIsSystemTemplate());
        return res;
    }

    public static List<ScreeningQuestionTemplateResMin> fromList(List<ScreeningQuestionTemplate> entities) {
        return entities.stream().map(ScreeningQuestionTemplateResMin::from).toList();
    }

    public static List<ScreeningQuestionTemplateResMin> fromResList(List<ScreeningQuestionTemplateRes> entities) {
        return entities.stream().map(ScreeningQuestionTemplateResMin::from).toList();
    }
}
