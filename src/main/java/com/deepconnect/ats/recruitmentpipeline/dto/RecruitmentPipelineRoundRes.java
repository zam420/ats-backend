package com.deepconnect.ats.recruitmentpipeline.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;

@Data
public class RecruitmentPipelineRoundRes {
    private String id;
    private String name;
    private String description;
    private Integer roundType;
    private List<String> interviewers;
    private Integer order;

    public static RecruitmentPipelineRoundRes from(RecruitmentPipelineRound entity) {
        if(entity == null)
            return null;
        RecruitmentPipelineRoundRes res = new RecruitmentPipelineRoundRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setRoundType(entity.getRoundType());
        res.setOrder(entity.getOrder());
        return res;
    }

    public static List<RecruitmentPipelineRoundRes> fromList(List<RecruitmentPipelineRound> entities) {
        return entities.stream().map(RecruitmentPipelineRoundRes::from).toList();
    }
}
