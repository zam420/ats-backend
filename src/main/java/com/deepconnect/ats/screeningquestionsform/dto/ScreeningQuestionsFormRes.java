package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;

@Data
public class ScreeningQuestionsFormRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private String ownerId;
    // Round names in order, joined as "name1 -> name2"; generated on the fly, never stored
    private String questionSequence;
    
    public static ScreeningQuestionsFormRes from(ScreeningQuestionsForm entity) {
        ScreeningQuestionsFormRes res = new ScreeningQuestionsFormRes();
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

    public static List<ScreeningQuestionsFormRes> fromList(List<ScreeningQuestionsForm> entities) {
        return entities.stream().map(ScreeningQuestionsFormRes::from).toList();
    }
}
