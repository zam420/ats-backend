package com.deepconnect.ats.recruitmentpipeline.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundBase;

@Data
public class RecruitmentPipelineRoundReqRes {
    private String id;
    private String name;
    private String description;
    private RecruitmentPipelineRoundType roundType;
    private Integer order;

    public static RecruitmentPipelineRoundReqRes from(RecruitmentPipelineRoundBase entity) {
        if(entity == null)
            return null;
        RecruitmentPipelineRoundReqRes res = new RecruitmentPipelineRoundReqRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setRoundType(entity.getRoundType());
        res.setOrder(entity.getOrder());
        return res;
    }

    public static List<RecruitmentPipelineRoundReqRes> from(List<? extends RecruitmentPipelineRoundBase> entities) {
        return entities.stream().map(RecruitmentPipelineRoundReqRes::from).toList();
    }
}
