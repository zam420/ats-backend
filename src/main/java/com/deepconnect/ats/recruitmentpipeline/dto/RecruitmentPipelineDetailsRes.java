package com.deepconnect.ats.recruitmentpipeline.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;

@Data
public class RecruitmentPipelineDetailsRes {
    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isSystemTemplate;
    private Boolean isSharedWithinTenant;
    private List<RecruitmentPipelineRoundRes> rounds;
    private Set<String> sharedWithUserIds;
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
        if(!CollectionUtils.isEmpty(entity.getSharedWithUsers())){
            res.setIsPublic(false);
            res.setIsSharedWithinTenant(true);
        }
        res.setSharedWithUserIds(
            entity.getSharedWithUsers() == null
                ? Set.of()
                : entity.getSharedWithUsers().stream().map(u -> u.getUser().getPublicId()).collect(Collectors.toSet())
        );
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<RecruitmentPipelineDetailsRes> fromList(List<RecruitmentPipeline> entities) {
        return entities.stream().map(RecruitmentPipelineDetailsRes::from).toList();
    }
}
