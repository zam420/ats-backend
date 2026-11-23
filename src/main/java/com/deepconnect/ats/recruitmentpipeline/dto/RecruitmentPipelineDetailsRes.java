package com.deepconnect.ats.recruitmentpipeline.dto;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;

@Getter
@Setter
public class RecruitmentPipelineDetailsRes extends RecruitmentPipelineResBase {
    
    private List<RecruitmentPipelineRoundRes> rounds;
    private Set<String> sharedWithUserIds;
    private String ownerId;

    protected RecruitmentPipelineDetailsRes(RecruitmentPipeline entity, String ownerName) {
        super(entity, ownerName);
    }

    public static RecruitmentPipelineDetailsRes from(RecruitmentPipeline entity, String ownerName) {
        if(entity == null)
            return null;
        RecruitmentPipelineDetailsRes res = new RecruitmentPipelineDetailsRes(entity, ownerName);
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

    public static List<RecruitmentPipelineDetailsRes> fromList(List<RecruitmentPipeline> entities, HashMap<Long, String> ownerNamesById) {
        return entities.stream().map(e -> RecruitmentPipelineDetailsRes.from(e, ownerNamesById.get(e.getOwnerId()))).toList();
    }
}
