package com.deepconnect.ats.recruitmentpipeline.dto;

import lombok.Getter;
import lombok.Setter;

import org.springframework.util.CollectionUtils;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;

@Getter
@Setter 
public class RecruitmentPipelineRes extends RecruitmentPipelineResBase {
    
    private String roundSequence;// Round names in order, joined as "name1 -> name2"; generated on the fly, never stored

    protected RecruitmentPipelineRes(RecruitmentPipeline entity) {
        super(entity);
    }
    
    public static RecruitmentPipelineRes from(RecruitmentPipeline entity) {
        RecruitmentPipelineRes res = new RecruitmentPipelineRes(entity);
        if (!CollectionUtils.isEmpty(entity.getRounds())) {
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
