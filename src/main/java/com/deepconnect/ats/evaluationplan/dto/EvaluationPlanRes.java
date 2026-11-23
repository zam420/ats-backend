package com.deepconnect.ats.evaluationplan.dto;

import lombok.Getter;
import lombok.Setter;

import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRound;

@Getter
@Setter 
public class EvaluationPlanRes extends EvaluationPlanResBase {
    
    private String roundSequence;// Round names in order, joined as "name1 -> name2"; generated on the fly, never stored

    protected EvaluationPlanRes(EvaluationPlan entity) {
        super(entity);
    }
    
    public static EvaluationPlanRes from(EvaluationPlan entity) {
        EvaluationPlanRes res = new EvaluationPlanRes(entity);        
        if (!CollectionUtils.isEmpty(entity.getRounds())) {
            res.setRoundSequence(entity.getRounds().stream()
                .sorted(Comparator.comparing(EvaluationPlanRound::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(EvaluationPlanRound::getName)
                .filter(n -> n != null && !n.isBlank())
                .collect(Collectors.joining(" -> ")));
        }
        return res;
    }

    public static List<EvaluationPlanRes> fromList(List<EvaluationPlan> entities) {
        return entities.stream().map(EvaluationPlanRes::from).toList();
    }
}
