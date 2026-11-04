package com.deepconnect.ats.recruitmentpipeline.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;

@Data
public class RecruitmentPipelineDetailsRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private List<RecruitmentPipelineRoundRes> rounds;
    private String ownerId;

    public static RecruitmentPipelineDetailsRes from(RecruitmentPipeline entity) {
        if(entity == null)
            return null;
        RecruitmentPipelineDetailsRes res = new RecruitmentPipelineDetailsRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        res.setRounds(
        entity.getRounds() == null
            ? List.of()
            : entity.getRounds().stream().map(RecruitmentPipelineRoundRes::from).toList()
    );
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<RecruitmentPipelineDetailsRes> fromList(List<RecruitmentPipeline> entities) {
        return entities.stream().map(RecruitmentPipelineDetailsRes::from).toList();
    }
}
