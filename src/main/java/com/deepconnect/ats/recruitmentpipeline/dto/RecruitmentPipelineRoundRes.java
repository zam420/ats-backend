package com.deepconnect.ats.recruitmentpipeline.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundBase;

@Data
public class RecruitmentPipelineRoundRes {
    private String id;
    private String name;
    private String description;
    private RecruitmentPipelineRoundType roundType;
    private Integer order;

    public static RecruitmentPipelineRoundRes from(RecruitmentPipelineRoundBase entity) {
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

    public static List<RecruitmentPipelineRoundRes> fromList(List<RecruitmentPipelineRoundBase> entities) {
        return entities.stream().map(RecruitmentPipelineRoundRes::from).toList();
    }
}
