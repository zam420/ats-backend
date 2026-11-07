package com.deepconnect.ats.recruitmentpipeline.dto;

import lombok.Data;
import java.util.List;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;

@Data
public class RecruitmentPipelineResMin {
    private String id;
    private String name;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    
    public static RecruitmentPipelineResMin from(RecruitmentPipeline entity) {
        RecruitmentPipelineResMin res = new RecruitmentPipelineResMin();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        return res;
    }

    public static RecruitmentPipelineResMin from(RecruitmentPipelineRes entity) {
        RecruitmentPipelineResMin res = new RecruitmentPipelineResMin();
        res.setId(entity.getId());
        res.setName(entity.getName());
        res.setIsPublic(entity.getIsPublic());
        res.setIsSystemTemplate(entity.getIsSystemTemplate());
        return res;
    }

    public static List<RecruitmentPipelineResMin> fromList(List<RecruitmentPipeline> entities) {
        return entities.stream().map(RecruitmentPipelineResMin::from).toList();
    }

    public static List<RecruitmentPipelineResMin> fromResList(List<RecruitmentPipelineRes> entities) {
        return entities.stream().map(RecruitmentPipelineResMin::from).toList();
    }
}
