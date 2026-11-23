package com.deepconnect.ats.common.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.user.dto.UserSummary;

@Data
public class TemplateFiltersRes {

    private List<EnumsExtendedRes> controlTypes;
    private List<UserSummary> users;
    
    public static TemplateFiltersRes from(
        List<EnumsExtendedRes> controlTypes, 
        List<UserSummary> users) 
    {
        TemplateFiltersRes res = new TemplateFiltersRes();
        res.setControlTypes(controlTypes);
        res.setUsers(users);
        return res;
    }

}
