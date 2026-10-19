package com.deepconnect.ats.interviewprocess.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.interviewprocess.model.InterviewProcess;
import com.deepconnect.ats.interviewprocess.model.InterviewProcessRound;

@Data
public class InterviewProcessDetailsRes {
    private String id;
    private String name;
    private Integer workLocation;
    private String notes;
    private Integer status;
    private List<InterviewProcessRound> rounds;

    public static InterviewProcessDetailsRes from(InterviewProcess entity) {
        if(entity == null)
            return null;
        InterviewProcessDetailsRes res = new InterviewProcessDetailsRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setWorkLocation(entity.getWorkLocation());
        res.setNotes(entity.getNotes());
        res.setStatus(entity.getStatus());
        //res.setRounds(entity.getRounds());
        res.setRounds(
        entity.getRounds() == null
            ? List.of()
            : entity.getRounds().stream().toList()
    );
        return res;
    }

    public static List<InterviewProcessDetailsRes> fromList(List<InterviewProcess> entities) {
        return entities.stream().map(InterviewProcessDetailsRes::from).toList();
    }
}
