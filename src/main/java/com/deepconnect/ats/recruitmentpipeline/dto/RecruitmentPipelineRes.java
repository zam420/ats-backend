package com.deepconnect.ats.recruitmentpipeline.dto;

import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;

@Data
public class RecruitmentPipelineRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private Boolean isSharedWithinTenant;
    private String ownerId;
    // Round names in order, joined as "name1 -> name2"; generated on the fly, never stored
    private String roundSequence;
    
    public static RecruitmentPipelineRes from(RecruitmentPipeline entity) {
        RecruitmentPipelineRes res = new RecruitmentPipelineRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setIsPublic(entity.getIsPublicForTenant());
        res.setIsSystemTemplate(entity.getIsPublicForAll());
        res.setIsSharedWithinTenant(entity.getIsSharedWithinTenant());
        res.setOwnerId(entity.getOwnerPublicId());
        if (entity.getRounds() != null) {
            res.setRoundSequence(entity.getRounds().stream()
                .sorted(Comparator.comparing(RecruitmentPipelineRound::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(RecruitmentPipelineRound::getName)
                .filter(n -> n != null && !n.isBlank())
                .collect(Collectors.joining(" -> ")));
        }
        return res;
    }

    public static List<RecruitmentPipelineRes> fromList(List<RecruitmentPipeline> entities) {
        return entities.stream().map(RecruitmentPipelineRes::from).toList();
    }
}
