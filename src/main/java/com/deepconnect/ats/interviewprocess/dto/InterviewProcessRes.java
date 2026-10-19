package com.deepconnect.ats.interviewprocess.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.interviewprocess.model.InterviewProcess;

@Data
public class InterviewProcessRes {
    private String id;
    private String name;
    private Integer workLocation;
    private String notes;
    private Integer status;

    public static InterviewProcessRes from(InterviewProcess entity) {
        InterviewProcessRes res = new InterviewProcessRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setWorkLocation(entity.getWorkLocation());
        res.setNotes(entity.getNotes());
        res.setStatus(entity.getStatus());
        return res;
    }

    public static List<InterviewProcessRes> fromList(List<InterviewProcess> entities) {
        return entities.stream().map(InterviewProcessRes::from).toList();
    }
}
