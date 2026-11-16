package com.deepconnect.ats.screeningquestionsform.service;

import com.deepconnect.ats.common.utils.StringUtils;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionBase;

import java.util.List;
import java.util.Objects;

public final class ScreeningQuestionHelper {
    private ScreeningQuestionHelper() {
    }

    /**
     * Checks whether two lists of screening questions are equal. Null lists are treated as empty.
     * The lists are equal when they have the same size and, comparing position by position,
     * every pair has the same order, question type, label, description, options, default option,
     * mandatory flag, multi-select flag, character limit and heading level. Label and description
     * are compared with null and blank treated as the same value. Options are compared in sequence.
     *
     * @param existing the screening questions currently stored
     * @param incoming the screening questions from the request
     * @return true if both lists are equivalent, false otherwise
     */
    public static boolean questionsEqual(List<? extends ScreeningQuestionBase> existing, List<? extends ScreeningQuestionBase> incoming) {
        List<? extends ScreeningQuestionBase> a = existing == null ? List.of() : existing;
        List<? extends ScreeningQuestionBase> b = incoming == null ? List.of() : incoming;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            ScreeningQuestionBase x = a.get(i);
            ScreeningQuestionBase y = b.get(i);
            if (!Objects.equals(x.getOrder(), y.getOrder())
                    || !Objects.equals(x.getScreeningQuestionTypeId(), y.getScreeningQuestionTypeId())
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getLabel()), StringUtils.blankToNullAndTrim(y.getLabel()))
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getDescription()), StringUtils.blankToNullAndTrim(y.getDescription()))
                    || !Objects.equals(x.getOptions(), y.getOptions())
                    || !Objects.equals(x.getDefaultOption(), y.getDefaultOption())
                    || !Objects.equals(x.getIsMandatory(), y.getIsMandatory())
                    || !Objects.equals(x.getIsDropDownMultiSelect(), y.getIsDropDownMultiSelect())
                    || !Objects.equals(x.getMaxChars(), y.getMaxChars())
                    || !Objects.equals(x.getHeadingLevel(), y.getHeadingLevel())) {
                return false;
            }
        }
        return true;
    }

}
