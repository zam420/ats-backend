package com.deepconnect.ats.screeningquestionsform.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.common.dto.EnumsExtendedRes;
import com.deepconnect.ats.user.dto.UserSummary;

@Data
public class ScreeningQuestionsFormFiltersRes {

    private List<EnumsExtendedRes> controlTypes;
    private List<UserSummary> users;
    
    public static ScreeningQuestionsFormFiltersRes from(
        List<EnumsExtendedRes> controlTypes, 
        List<UserSummary> users) 
    {
        ScreeningQuestionsFormFiltersRes res = new ScreeningQuestionsFormFiltersRes();
        res.setControlTypes(controlTypes);
        res.setUsers(users);
        return res;
    }

}
