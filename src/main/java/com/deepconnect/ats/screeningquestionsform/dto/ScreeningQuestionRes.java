package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Data;
import com.deepconnect.ats.screeningquestionsform.enums.ScreeningQuestionType;
import java.util.List;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionBase;

@Data
public class ScreeningQuestionRes {
    private String id;
    private String description;
    private ScreeningQuestionType screeningQuestionType;
    private Integer order;

    private String label;//label is for display to candidates e.g. how is this question labaled for candidates
    private List<String> options;//Options for checkbox, radio buttons, or dropdown questions
    private String defaultOption;
    private Boolean isMandatory;
    private Boolean isDropDownMultiSelect;
    private Integer maxChars;

    public static ScreeningQuestionRes from(ScreeningQuestionBase entity) {
        if(entity == null)
            return null;
        ScreeningQuestionRes res = new ScreeningQuestionRes();
        res.setId(entity.getPublicId());
        res.setDescription(entity.getDescription());
        res.setScreeningQuestionType(entity.getScreeningQuestionType());
        res.setOrder(entity.getOrder());
        res.setLabel(entity.getLabel());
        res.setOptions(entity.getOptions());
        res.setDefaultOption(entity.getDefaultOption());
        res.setIsMandatory(entity.getIsMandatory());
        res.setIsDropDownMultiSelect(entity.getIsDropDownMultiSelect());
        res.setMaxChars(entity.getMaxChars());
        return res;
    }

    public static List<ScreeningQuestionRes> fromList(List<ScreeningQuestionBase> entities) {
        return entities.stream().map(ScreeningQuestionRes::from).toList();
    }
}
