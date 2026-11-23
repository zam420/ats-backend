package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;

@Getter 
@Setter 
public class ScreeningQuestionsFormRes extends ScreeningQuestionsFormResBase {
    
    protected ScreeningQuestionsFormRes(ScreeningQuestionsForm entity, String ownerName) {
        super(entity, ownerName);
    }

    public static ScreeningQuestionsFormRes from(ScreeningQuestionsForm entity, String ownerName) {
        return new ScreeningQuestionsFormRes(entity, ownerName);
    }

    public static List<ScreeningQuestionsFormRes> from(List<ScreeningQuestionsForm> entities, HashMap<Long, String> ownerNamesById) {
        return entities.stream().map(e -> ScreeningQuestionsFormRes.from(e, ownerNamesById.get(e.getOwnerId()))).toList();
    }

}