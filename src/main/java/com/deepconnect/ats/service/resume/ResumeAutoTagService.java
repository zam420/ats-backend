package com.deepconnect.ats.service.resume;

import com.deepconnect.ats.common.constant.enums.TagMatchType;
import com.deepconnect.ats.model.CandidateEducationHistory;
import com.deepconnect.ats.model.CandidateProjectExperience;
import com.deepconnect.ats.model.CandidateWorkExperience;
import com.deepconnect.ats.model.Tag;
import com.deepconnect.ats.repository.ScheduleJobQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** Port of Django resume_tag_service (auto tagging). */
@Service
@RequiredArgsConstructor
public class ResumeAutoTagService {

    private final ScheduleJobQueryRepository queryRepository;

    /** Returns the match type value (or null) via a 2-element holder: [count, matchTypeValue|null]. */
    public record MatchResult(int count, Integer matchType) {
    }

    public void autoTagResumes(List<CandidateResumeAggregate> aggregates, List<Tag> tags) {
        if (aggregates.isEmpty() || tags.isEmpty()) {
            return;
        }
        List<Object[]> rows = new ArrayList<>();
        for (CandidateResumeAggregate aggregate : aggregates) {
            for (Tag tag : tags) {
                MatchResult m = matchResumeTagWithCount(aggregate, tag);
                if (m.count() > 0) {
                    rows.add(new Object[]{aggregate.resume().getId(), tag.getId(), m.matchType(), m.count()});
                }
            }
        }
        if (!rows.isEmpty()) {
            queryRepository.upsertResumeTags(rows);
        }
    }

    public MatchResult matchResumeTagWithCount(CandidateResumeAggregate aggregate, Tag tag) {
        int count = countCompletelyMatch(aggregate, tag);
        if (count > 0) {
            return new MatchResult(count, TagMatchType.FULLY_MATCH.getValue());
        }
        count = countPartiallyMatch(aggregate, tag);
        return new MatchResult(count, count > 0 ? TagMatchType.PARTIAL_MATCH.getValue() : null);
    }

    private int countCompletelyMatch(CandidateResumeAggregate a, Tag tag) {
        int c = 0;
        for (CandidateEducationHistory e : a.eduHistories()) {
            c += countTargetWordInContent(tag.getName(), e.getMajor());
        }
        for (CandidateWorkExperience e : a.workHistories()) {
            c += countTargetWordInContent(tag.getName(), e.getMajorAchievement());
        }
        for (CandidateProjectExperience e : a.projectHistories()) {
            c += countTargetWordInContent(tag.getName(), e.getProjectDesc());
        }
        return c;
    }

    private int countPartiallyMatch(CandidateResumeAggregate a, Tag tag) {
        int c = 0;
        for (CandidateEducationHistory e : a.eduHistories()) {
            c += partiallyMatchCount(tag.getName(), e.getMajor());
        }
        for (CandidateWorkExperience e : a.workHistories()) {
            c += partiallyMatchCount(tag.getName(), e.getMajorAchievement());
        }
        for (CandidateProjectExperience e : a.projectHistories()) {
            c += partiallyMatchCount(tag.getName(), e.getProjectDesc());
        }
        return c;
    }

    private int partiallyMatchCount(String tagName, String content) {
        String[] parts = tagName.split(" ", -1);
        int n = parts.length;
        int partsToMatch = n == 1 ? 1
                : new BigDecimal(n).multiply(new BigDecimal(0.8)).setScale(0, RoundingMode.FLOOR).intValue();
        int matched = 0;
        for (String part : parts) {
            matched += countTargetWordInContent(part, content);
        }
        return new BigDecimal(matched).divide(new BigDecimal(partsToMatch), MathContext.DECIMAL128)
                .setScale(0, RoundingMode.HALF_DOWN).intValue();
    }

    // ---- string_util.count_target_word_in_content ----

    static int countTargetWordInContent(String target, String content) {
        boolean targetBlank = target == null || target.isBlank();
        boolean contentBlank = content == null || content.isBlank();
        if (targetBlank != contentBlank) {
            return 0;
        }
        if (content == null) {
            return 0;
        }
        String t = target.toLowerCase();
        String c = content.toLowerCase();
        int count = 0;
        int idx = find(c, t, 0);
        while (idx != -1) {
            int end = idx + t.length();
            boolean startOk = idx == 0 || !isAlnum(c.charAt(idx - 1)) || isSegment(c.charAt(idx - 1), c.charAt(idx));
            boolean endOk = end == c.length() || !isAlnum(c.charAt(end))
                    || isSegment(c.charAt(end - 1), c.charAt(end));
            if (startOk && endOk) {
                count++;
            }
            idx = find(c, t, idx + 1);
        }
        return count;
    }

    private static int find(String s, String sub, int from) {
        if (from > s.length()) {
            return -1;
        }
        return s.indexOf(sub, from);
    }

    private static boolean isSegment(char c1, char c2) {
        if (isLetter(c1)) {
            return !isLetter(c2);
        }
        return !isChinese(c2);
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isAlnum(char c) {
        return isLetter(c) || (c >= '0' && c <= '9');
    }

    private static boolean isChinese(char c) {
        return c >= 0x4e00 && c <= 0x9fff;
    }
}
