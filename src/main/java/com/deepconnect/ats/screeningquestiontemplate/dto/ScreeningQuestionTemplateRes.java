package com.deepconnect.ats.screeningquestiontemplate.dto;

import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestionTemplate;
import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestion;

@Data
public class ScreeningQuestionTemplateRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private String ownerId;
    // Round names in order, joined as "name1 -> name2"; generated on the fly, never stored
    private String questionSequence;
    
    public static ScreeningQuestionTemplateRes from(ScreeningQuestionTemplate entity) {
        ScreeningQuestionTemplateRes res = new ScreeningQuestionTemplateRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        res.setOwnerId(entity.getOwnerPublicId());
        if (entity.getQuestions() != null) {
            res.setQuestionSequence(entity.getQuestions().stream()
                .sorted(Comparator.comparing(ScreeningQuestion::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(ScreeningQuestion::getName)
                .filter(n -> n != null && !n.isBlank())
                .collect(Collectors.joining(" -> ")));
        }
        return res;
    }

    public static List<ScreeningQuestionTemplateRes> fromList(List<ScreeningQuestionTemplate> entities) {
        return entities.stream().map(ScreeningQuestionTemplateRes::from).toList();
    }
}
