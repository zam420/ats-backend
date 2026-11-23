package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;

@Getter 
@Setter 
public class ScreeningQuestionsFormRes extends ScreeningQuestionsFormResBase {
    
    protected ScreeningQuestionsFormRes(ScreeningQuestionsForm entity) {
        super(entity);
    }

    public static ScreeningQuestionsFormRes from(ScreeningQuestionsForm entity) {
        return new ScreeningQuestionsFormRes(entity);
    }

    public static List<ScreeningQuestionsFormRes> fromList(List<ScreeningQuestionsForm> entities) {
        return entities.stream().map(ScreeningQuestionsFormRes::from).toList();
    }

}