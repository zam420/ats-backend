package com.deepconnect.ats.service;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.DuplicationCheckBody;
import com.deepconnect.ats.model.Candidate;
import com.deepconnect.ats.model.CandidateEducationHistory;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.CandidateResumeDuplication;
import com.deepconnect.ats.model.CandidateWorkExperience;
import com.deepconnect.ats.model.JobPosition;
import com.deepconnect.ats.model.User;
import com.deepconnect.ats.repository.CandidateEducationHistoryRepository;
import com.deepconnect.ats.repository.CandidateRepository;
import com.deepconnect.ats.repository.CandidateResumeDuplicationRepository;
import com.deepconnect.ats.repository.CandidateResumeRepository;
import com.deepconnect.ats.repository.CandidateWorkExperienceRepository;
import com.deepconnect.ats.repository.JobPositionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateDuplicationService {
    public static final int RESUME_NOT_FOUND_CODE = 3011;
    private static final int DELETED_DATA_STATUS = 2;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final CandidateRepository candidateRepository;
    private final CandidateResumeRepository candidateResumeRepository;
    private final CandidateEducationHistoryRepository educationHistoryRepository;
    private final CandidateWorkExperienceRepository workExperienceRepository;
    private final CandidateResumeDuplicationRepository duplicationRepository;
    private final JobPositionRepository jobPositionRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public Object checkResumeDuplication(DuplicationCheckBody requestBody) {
        LocalDateTime checkTime = LocalDateTime.now();
        Long requestResumeId = requestBody.getResumeId();
        CandidateResume requestResume = candidateResumeRepository.findById(requestResumeId)
                .orElseThrow(() -> new InternalServerException("未找到简历: Candidate Resume"));
        Candidate candidate = candidateRepository.findById(requestResume.getCandidateId())
                .orElseThrow(() -> new InternalServerException("Candidate not found: " + requestResume.getCandidateId()));

        List<Map<String, Object>> duplicates = new ArrayList<>();
        List<Map<String, Object>> exactRows = exactMatchCandidates(candidate);
        List<Long> excludedCandidateIds = new ArrayList<>();
        Set<Long> exactCandidateIds = new HashSet<>();
        for (Map<String, Object> row : exactRows) {
            Long candidateId = asLong(row.get("id"));
            if (candidateId == null) continue;
            excludedCandidateIds.add(candidateId);
            if (candidateId.equals(requestResume.getCandidateId())) continue;
            Map<String, Object> duplicate = new LinkedHashMap<>();
            duplicate.put("candidate_id", candidateId);
            duplicate.put("candidate_name", row.get("name"));
            duplicate.put("phone_number", row.get("phone_number"));
            duplicate.put("email", row.get("email"));
            duplicate.put("resumes", new ArrayList<Map<String, Object>>());
            duplicates.add(duplicate);
            exactCandidateIds.add(candidateId);
        }

        List<CandidateResume> exactResumes = new ArrayList<>();
        for (Long candidateId : exactCandidateIds) {
            candidateResumeRepository.findByCandidateId(candidateId).stream()
                    .filter(resume -> !Integer.valueOf(DELETED_DATA_STATUS).equals(resume.getDataStatus()))
                    .forEach(exactResumes::add);
        }
        List<CandidateEducationHistory> education = educationHistoryRepository.findByCandidateResumeId(requestResumeId);
        List<CandidateWorkExperience> work = workExperienceRepository.findByCandidateResumeId(requestResumeId);
        CandidateEducationHistory highestEducation = education.stream()
                .max(Comparator.comparing(CandidateEducationHistory::getDegree,
                                Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(CandidateEducationHistory::getDateTo,
                                Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElse(null);
        String highestUniversity = highestEducation == null ? null : highestEducation.getUniversity();
        List<Map<String, Object>> workArray = new ArrayList<>();
        for (CandidateWorkExperience experience : work) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("company", experience.getCompanyName());
            item.put("from", dateString(experience.getDateFrom()));
            item.put("to", dateString(experience.getDateTo()));
            workArray.add(item);
        }
        String likeName = candidate.getName() == null ? "" : candidate.getName();
        if (likeName.endsWith("先生") || likeName.endsWith("女士")) likeName = likeName.substring(0, likeName.length() - 2);
        List<Long> similarResumeIds = similarResumeIds(likeName, highestUniversity, workArray, excludedCandidateIds);
        Map<String, Object> highestEducationData = new LinkedHashMap<>();
        highestEducationData.put("university", highestUniversity);
        highestEducationData.put("from", highestEducation == null ? null : dateString(highestEducation.getDateFrom()));
        highestEducationData.put("to", highestEducation == null ? null : dateString(highestEducation.getDateTo()));
        List<Map<String, Object>> similarRows = similarResumes(likeName, highestEducationData, workArray, similarResumeIds);
        Map<Long, String> jobNames = positionTitles(exactResumes, similarRows);

        for (Map<String, Object> duplicate : duplicates) {
            Long duplicateCandidateId = asLong(duplicate.get("candidate_id"));
            List<Map<String, Object>> resumeResults = castMapList(duplicate.get("resumes"));
            for (CandidateResume resume : exactResumes) {
                if (!duplicateCandidateId.equals(resume.getCandidateId())
                        || requestResumeId.equals(resume.getId())
                        || requestResume.getCandidateId().equals(resume.getCandidateId())) continue;
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("resume_id", resume.getId());
                result.put("job_position_id", resume.getApplyingPositionId());
                result.put("job_position_name", jobNames.get(resume.getApplyingPositionId()));
                result.put("applied_at", resume.getAppliedAt() == null ? null : resume.getAppliedAt().toLocalDate().format(DATE_FORMAT));
                result.put("full_match", true);
                result.put("match_rate", 100);
                resumeResults.add(result);
            }
        }

        Map<Long, List<Map<String, Object>>> similarByCandidate = new LinkedHashMap<>();
        for (Map<String, Object> row : similarRows) {
            Long candidateId = asLong(row.get("candidate_id"));
            if (candidateId != null) similarByCandidate.computeIfAbsent(candidateId, ignored -> new ArrayList<>()).add(row);
        }
        for (Map.Entry<Long, List<Map<String, Object>>> entry : similarByCandidate.entrySet()) {
            if (entry.getKey().equals(requestResume.getCandidateId())) continue;
            List<Map<String, Object>> matches = entry.getValue();
            if (matches.isEmpty()) {
                log.warn("check_resume_duplication: {}  no resumes", entry.getKey());
                continue;
            }
            matches.sort(Comparator.comparing((Map<String, Object> row) -> asInt(row.get("match_rate")),
                    Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            Map<String, Object> best = matches.get(0);
            Map<String, Object> duplicate = new LinkedHashMap<>();
            duplicate.put("candidate_name", best.get("name"));
            duplicate.put("candidate_id", entry.getKey());
            duplicate.put("phone_number", "");
            duplicate.put("email", "");
            List<Map<String, Object>> resumeResults = new ArrayList<>();
            duplicate.put("resumes", resumeResults);
            duplicates.add(duplicate);
            for (Map<String, Object> match : matches) {
                Long positionId = asLong(match.get("position_id"));
                Integer matchRate = asInt(match.get("match_rate"));
                if (Integer.valueOf(99).equals(matchRate)) matchRate = 100;
                String appliedAt = match.get("applied_at") == null ? null : match.get("applied_at").toString();
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("resume_id", match.get("id"));
                result.put("job_position_id", positionId);
                result.put("job_position_name", jobNames.get(positionId));
                result.put("applied_at", appliedAt == null ? null : appliedAt.substring(0, Math.min(10, appliedAt.length())));
                result.put("full_match", false);
                result.put("match_rate", matchRate);
                result.put("candidate_name_match_rate", match.get("name_match_rate"));
                result.put("university_name_match_rate", match.get("edu_match_rate"));
                result.put("university_from_match_rate", match.get("edu_from_match_rate"));
                result.put("university_to_match_rate", match.get("edu_to_match_rate"));
                result.put("company_name_match_rate", match.get("work_match_rate"));
                result.put("company_from_match_rate", match.get("work_from_match_rate"));
                result.put("company_to_match_rate", match.get("work_to_match_rate"));
                resumeResults.add(result);
            }
        }

        User user = requestBody.getUser();
        duplicationRepository.save(CandidateResumeDuplication.builder()
                .userId(user == null ? null : user.getId())
                .candidateResumeId(requestResumeId)
                .checkTime(checkTime)
                .operations(List.of())
                .duplicateResumes(duplicates)
                .build());
        return duplicates;
    }

    public CandidateResumeDuplication getDuplicationByResumeId(Long resumeId) {
        return duplicationRepository.findByCandidateResumeId(resumeId).stream()
                .max(Comparator.comparing(CandidateResumeDuplication::getId)).orElse(null);
    }

    private List<Map<String, Object>> exactMatchCandidates(Candidate candidate) {
        String phone = candidate.getPhoneNumber();
        String email = candidate.getEmail();
        if (phone != null && phone.trim().startsWith("**")) phone = "";
        if (email != null && !email.matches("(?i)^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?)+$")) email = "";
        return jdbcTemplate.queryForList("CALL candidateDuplicationExactMatch(?, ?)", phone, email);
    }

    private List<Long> similarResumeIds(String name, String university, List<Map<String, Object>> work,
                                        List<Long> excludedCandidateIds) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("CALL querySimilarResumeIds(?, ?, ?, ?)",
                name, university, toJson(work), toJson(excludedCandidateIds));
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Long id = asLong(row.values().iterator().next());
            if (id != null) ids.add(id);
        }
        return ids;
    }

    private List<Map<String, Object>> similarResumes(String name, Map<String, Object> highestEducation,
                                                     List<Map<String, Object>> work, List<Long> ids) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("CALL resumeDuplicationSimilarMatch(?, ?, ?, ?)",
                name, toJson(highestEducation), toJson(work), toJson(ids));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Object value = row.values().iterator().next();
            try {
                JsonNode parsed = objectMapper.readTree(String.valueOf(value));
                if (parsed.isTextual()) parsed = objectMapper.readTree(parsed.asText());
                @SuppressWarnings("unchecked")
                Map<String, Object> converted = objectMapper.convertValue(parsed, Map.class);
                result.add(converted);
            } catch (JsonProcessingException e) {
                throw new InternalServerException("Invalid resume duplication result from database");
            }
        }
        return result;
    }

    private Map<Long, String> positionTitles(List<CandidateResume> resumes, List<Map<String, Object>> similarRows) {
        Set<Long> ids = new HashSet<>();
        for (CandidateResume resume : resumes) {
            if (resume.getApplyingPositionId() != null) ids.add(resume.getApplyingPositionId());
        }
        for (Map<String, Object> row : similarRows) {
            Long id = asLong(row.get("position_id"));
            if (id != null) ids.add(id);
        }
        Map<Long, String> titles = new HashMap<>();
        for (JobPosition position : jobPositionRepository.findAllById(ids)) titles.put(position.getId(), position.getTitle());
        return titles;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new InternalServerException("Unable to serialize duplication query parameters");
        }
    }

    private static String dateString(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMAT);
    }

    private static Long asLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.longValue();
        try { return Long.valueOf(value.toString()); } catch (NumberFormatException ignored) { return null; }
    }

    private static Integer asInt(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.intValue();
        try { return Integer.valueOf(value.toString()); } catch (NumberFormatException ignored) { return null; }
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> castMapList(Object value) {
        return (List<Map<String, Object>>) value;
    }
}
