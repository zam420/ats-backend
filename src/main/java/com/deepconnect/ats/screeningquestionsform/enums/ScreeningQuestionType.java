package com.deepconnect.ats.screeningquestionsform.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseExtendedEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum ScreeningQuestionType  implements LocalizedBaseExtendedEnum {

    INPUT_BOX(0, "inputBox"),
    INPUT_AREA(1, "inputArea"),
    CHECKBOX(2, "checkbox"),
    RADIO(3, "radioButton"),
    DROPDOWN(4, "dropdown"),
    LABEL(5, "label");

    @JsonValue
    private final Integer id;

    private final String key;
    private final String code;

    private static final String enumsKeyPrefix = "enums.screeningQuestionsType.";

    ScreeningQuestionType(Integer id, String key) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
        this.code = this.name();
    }

    
}