package com.deepconnect.ats.evaluationplan.service;

import com.deepconnect.ats.common.utils.StringUtils;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundBase;

import java.util.List;
import java.util.Objects;

public final class EvaluationPlanRoundHelper {
    private EvaluationPlanRoundHelper() {
    }

    /**
     * Checks whether two lists of rounds are equal. Null lists are treated as empty.
     * The lists are equal when they have the same size and, comparing position by position,
     * every pair of rounds has the same order, round type, name, description and interviewers.
     * Name and description are compared with null and blank treated as the same value.
     * Interviewers are compared in sequence (same size, same ids in the same order).
     *
     * @param existing the rounds currently stored
     * @param incoming the rounds from the request
     * @return true if both lists are equivalent, false otherwise
     */
    public static boolean roundsEqual(List<? extends EvaluationPlanRoundBase> existing, List<? extends EvaluationPlanRoundBase> incoming) {
        List<? extends EvaluationPlanRoundBase> a = existing == null ? List.of() : existing;
        List<? extends EvaluationPlanRoundBase> b = incoming == null ? List.of() : incoming;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            EvaluationPlanRoundBase x = a.get(i);
            EvaluationPlanRoundBase y = b.get(i);
            if (!Objects.equals(x.getOrder(), y.getOrder())
                    || !Objects.equals(x.getRoundType(), y.getRoundType())
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getName()), StringUtils.blankToNullAndTrim(y.getName()))
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getDescription()), StringUtils.blankToNullAndTrim(y.getDescription()))) {
                return false;
            }
            List<String> xi = x.getInterviewers() == null ? List.of() : x.getInterviewers();
            List<String> yi = y.getInterviewers() == null ? List.of() : y.getInterviewers();
            if (xi.size() != yi.size())
                return false;
            for (int j = 0; j < xi.size(); j++) {
                if (!Objects.equals(String.valueOf(xi.get(j)), String.valueOf(yi.get(j))))
                    return false;
            }
        }
        return true;
    }

}
