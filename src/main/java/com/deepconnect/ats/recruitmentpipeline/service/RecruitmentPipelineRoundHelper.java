package com.deepconnect.ats.recruitmentpipeline.service;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.utils.StringUtils;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundBase;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class RecruitmentPipelineRoundHelper {
    private RecruitmentPipelineRoundHelper() {
    }

    public static void assertRoundTypeOrder(List<? extends RecruitmentPipelineRoundBase> addedRounds,
            String loggedInUserPublicId, String planPublicId) {
        Set<Integer> coreTypeIds = List.of(RecruitmentPipelineRoundType.values()).stream()
            .filter(type -> !Boolean.TRUE.equals(type.getIsCustom()))
            .map(RecruitmentPipelineRoundType::getId)
            .collect(Collectors.toSet());

        List<Integer> coreIdsInOrder = addedRounds.stream()
            .sorted(Comparator.comparing(RecruitmentPipelineRoundBase::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
            .map(r -> r.getRoundType().getId())
            .filter(coreTypeIds::contains)
            .toList();

        for (int i = 1; i < coreIdsInOrder.size(); i++) {
            if (coreIdsInOrder.get(i - 1) >= coreIdsInOrder.get(i)) {
                throw new InternalServerException("Invalid round type order. Plan: " + planPublicId + ", Request by: " + loggedInUserPublicId);
            }
        }
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
    public static boolean roundsEqual(List<? extends RecruitmentPipelineRoundBase> existing, List<? extends RecruitmentPipelineRoundBase> incoming) {
        List<? extends RecruitmentPipelineRoundBase> a = existing == null ? List.of() : existing;
        List<? extends RecruitmentPipelineRoundBase> b = incoming == null ? List.of() : incoming;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            RecruitmentPipelineRoundBase x = a.get(i);
            RecruitmentPipelineRoundBase y = b.get(i);
            if (!Objects.equals(x.getOrder(), y.getOrder())
                    || !Objects.equals(x.getRoundType(), y.getRoundType())
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getName()), StringUtils.blankToNullAndTrim(y.getName()))
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getDescription()), StringUtils.blankToNullAndTrim(y.getDescription()))) {
                return false;
            }
        }
        return true;
    }
}
