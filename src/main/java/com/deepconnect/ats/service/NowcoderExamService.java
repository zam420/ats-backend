package com.deepconnect.ats.service;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.Exam;
import com.deepconnect.ats.repository.CandidateResumeRepository;
import com.deepconnect.ats.repository.ExamRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NowcoderExamService {
    private static final int SUBMITTED_DATA_STATUS = 1;
    private static final int DELETED_DATA_STATUS = 2;
    private static final int EXAM_SCHEDULED = 0;
    private static final int EXAM_SUBMITTED = 2;
    private static final int EXAM_SYNCED = 3;
    private static final int CHEAT_PENDING = 0;
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private final ExamRepository examRepository;
    private final EntityManager entityManager;
    private final CandidateResumeRepository candidateResumeRepository;
    private final RestClient restClient;
    private final String baseUrl;
    private final String apiKey;
    private final String token;
    private final String ownerId;
    private final ObjectMapper objectMapper;

    public NowcoderExamService(ExamRepository examRepository,
                               EntityManager entityManager,
                               CandidateResumeRepository candidateResumeRepository,
                               RestClient.Builder restClientBuilder,
                               @Value("${ats.nowcoder.base-url:}") String baseUrl,
                               @Value("${ats.nowcoder.api-key:}") String apiKey,
                               @Value("${ats.nowcoder.token:}") String token,
                               @Value("${ats.nowcoder.owner-id:}") String ownerId,
                               ObjectMapper objectMapper) {
        this.examRepository = examRepository;
        this.entityManager = entityManager;
        this.candidateResumeRepository = candidateResumeRepository;
        this.restClient = restClientBuilder.build();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.token = token;
        this.ownerId = ownerId;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> getPapers() {
        if (ownerId == null || ownerId.isBlank()) throw new InternalServerException("ats.nowcoder.owner-id is not configured");
        JsonNode data = get("/papers/oid-" + ownerId, Map.of(
                "page", 1, "pageCount", 1000, "orderBy", 0, "order", 2, "progressType", 0));
        if (data.path("totalCount").asInt() == 0 || !data.path("datas").isArray()) return List.of();
        long now = Instant.now().getEpochSecond();
        List<Map<String, Object>> papers = new ArrayList<>();
        for (JsonNode paper : data.path("datas")) {
            if (paper.path("finished").asBoolean()) continue;
            if (!paper.path("forever").asBoolean() && paper.path("endTime").asLong() <= now) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> item = objectMapper.convertValue(paper, Map.class);
            papers.add(item);
        }
        return papers;
    }

    public boolean checkBalance(int paperId, int candidateCount) {
        JsonNode data = get("/papers/test_users/checkBalance",
                Map.of("paperId_userCounts", paperId + "_" + candidateCount));
        return data.path("moneyEnough").asBoolean(false);
    }

    public JsonNode createTestUsers(int paperId, List<String> resumeKeys, List<String> names) {
        if (resumeKeys.size() != names.size()) throw new InternalServerException("Resume key and name counts differ");
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        if (apiKey != null && !apiKey.isBlank()) form.add("api_key", apiKey);
        resumeKeys.forEach(key -> form.add("key", key));
        names.forEach(name -> form.add("name", sanitizeCandidateName(name)));
        form.add("failOnExist", "true");
        form.add("version", "0");
        form.add("notice", "false");
        return parseResponse(restClient.post().uri(endpointUri("/papers/" + paperId + "/test_users"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(this::addAuthorization).body(form).retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> { }).toEntity(JsonNode.class));
    }

    public Map<String, Object> deleteTestFromNowcoder(int paperId, int testId) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        if (apiKey != null && !apiKey.isBlank()) form.add("api_key", apiKey);
        JsonNode result = parseResponse(restClient.method(HttpMethod.DELETE)
                .uri(endpointUri("/papers/" + paperId + "/test_users/" + testId))
                .headers(this::addAuthorization).body(form).retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> { }).toEntity(JsonNode.class));
        return Map.of("message", result.isTextual() ? result.asText() : result);
    }

    public List<Exam> listTest(Long resumeId) {
        return examRepository.findByCandidateResumeId(resumeId).stream()
                .filter(exam -> Integer.valueOf(SUBMITTED_DATA_STATUS).equals(exam.getDataStatus())).toList();
    }

    public void updateWebReportUrl(Exam exam) {
        JsonNode data;
        try {
            data = get("/papers/usertestinfo", Map.of("paperId", exam.getPaperId(),
                    "userkey", String.valueOf(exam.getCandidateResumeId()), "needCheatInfo", "true"));
        } catch (NowcoderApiException e) {
            if (e.code == 1306) {
                exam.setStatus(4);
                exam.setDataStatus(DELETED_DATA_STATUS);
                examRepository.save(exam);
            }
            return;
        }
        if (data.isArray()) {
            if (data.isEmpty()) return;
            data = data.get(0);
        }
        String reportUrl = textOrNull(data.get("reportLink"));
        if (reportUrl != null && !reportUrl.isBlank()) {
            log.info("update_web_report_url(): {}, {}", exam.getId(), reportUrl);
            exam.setWebReportUrl(reportUrl);
            examRepository.save(exam);
        }
    }

    private static String sanitizeCandidateName(String name) {
        return name == null ? "" : name.replaceAll("[`~!@#$%^&*()+=|{}':;\\[\\]<>/?！￥…（）—【】‘；：”“’。、？]", " ");
    }
    @Transactional
    public List<Long> updateTestResult() {
        LocalDateTime now = LocalDateTime.now(ZONE);
        List<Exam> exams = examRepository.findAll().stream()
                .filter(exam -> Integer.valueOf(SUBMITTED_DATA_STATUS).equals(exam.getDataStatus()))
                .filter(exam -> Integer.valueOf(EXAM_SCHEDULED).equals(exam.getStatus())
                        || Integer.valueOf(CHEAT_PENDING).equals(exam.getCheatStatus())
                        || exam.getResultFile() == null)
                .filter(exam -> exam.getBeginTime() == null || !exam.getBeginTime().isAfter(now))
                .toList();
        for (Exam exam : exams) {
            if (Integer.valueOf(EXAM_SCHEDULED).equals(exam.getStatus())
                    || Integer.valueOf(CHEAT_PENDING).equals(exam.getCheatStatus())) {
                log.info("update_test_result(): _get_user_test_info: {}", exam.getId());
                updateUserTestInfo(exam);
            }
            if (Integer.valueOf(DELETED_DATA_STATUS).equals(exam.getDataStatus())) {
                log.info("update_test_result(): exam {} is deleted", exam.getId());
                saveUnlessChanged(exam);
                updateResumeStatus(exam);
                continue;
            }
            boolean needDownload = exam.getCheatStatus() != null
                    && !Integer.valueOf(CHEAT_PENDING).equals(exam.getCheatStatus())
                    && exam.getResultFile() == null;
            if (needDownload) {
                log.info("update_test_result(): exam {} download pdf", exam.getId());
                downloadResultPdf(exam);
                if (exam.getResultFile() != null) exam.setStatus(EXAM_SYNCED);
            }
            if (!wasConcurrentlyFinalized(exam)) {
                log.info("update_test_result(): exam {} save", exam.getId());
                examRepository.save(exam);
                updateResumeStatus(exam);
            } else {
                log.info("update_test_result(): exam {} don't save", exam.getId());
            }
        }
        List<Long> ids = new ArrayList<>(exams.size());
        for (Exam exam : exams) ids.add(exam.getId());
        return ids;
    }

    private void updateUserTestInfo(Exam exam) {
        JsonNode data;
        try {
            data = get("/papers/usertestinfo", Map.of(
                    "paperId", exam.getPaperId(),
                    "userkey", String.valueOf(exam.getCandidateResumeId()),
                    "needCheatInfo", "true"));
        } catch (NowcoderApiException e) {
            if (e.code == 1306) {
                exam.setStatus(4);
                exam.setDataStatus(DELETED_DATA_STATUS);
            }
            return;
        }
        if (data.isArray()) {
            if (data.isEmpty()) return;
            data = data.get(0);
        }
        exam.setWebReportUrl(textOrNull(data.get("reportLink")));
        exam.setResultUrl(textOrNull(data.get("pdfUrl")));
        exam.setPaperScore(floatOrNull(data.get("paperScore")));
        Integer testStatus = integerOrNull(data.get("status"));
        if (Integer.valueOf(2).equals(testStatus) || Integer.valueOf(4).equals(testStatus)) {
            exam.setStatus(EXAM_SUBMITTED);
            exam.setScore(floatOrNull(data.get("userScore")));
        }
        Integer cheatStatus = integerOrNull(data.get("cheatStatus"));
        exam.setCheatStatus(cheatStatus != null && cheatStatus >= 0 && cheatStatus <= 3 ? cheatStatus : null);
        Long finishTime = longOrNull(data.get("finishTime"));
        if (finishTime != null) exam.setTestTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(finishTime), ZONE));
    }

    private JsonNode get(String path, Map<String, ?> params) {
        return parseResponse(restClient.get().uri(apiUri(path, params))
                .headers(headers -> addAuthorization(headers)).retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> { })
                .toEntity(JsonNode.class));
    }

    private URI endpointUri(String path) {
        if (baseUrl == null || baseUrl.isBlank()) throw new InternalServerException("ats.nowcoder.base-url is not configured");
        return URI.create(baseUrl + (path.startsWith("/") ? path : "/" + path));
    }
    private URI apiUri(String path, Map<String, ?> params) {
        if (baseUrl == null || baseUrl.isBlank()) throw new InternalServerException("ats.nowcoder.base-url is not configured");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + (path.startsWith("/") ? path : "/" + path));
        if (apiKey != null && !apiKey.isBlank()) builder.queryParam("api_key", apiKey);
        params.forEach((key, value) -> builder.queryParam(key, value));
        return builder.build().encode().toUri();
    }

    private void addAuthorization(org.springframework.http.HttpHeaders headers) {
        if (token != null && !token.isBlank()) headers.setBearerAuth(token);
    }

    private JsonNode parseResponse(ResponseEntity<JsonNode> response) {
        JsonNode body = response.getBody();
        if (body == null || !body.has("code") || !body.has("msg")) {
            log.warn("_parse_response exception: malformed Nowcoder response");
            throw new InternalServerException("NOWCODER status code: " + response.getStatusCode().value());
        }
        int code = body.path("code").asInt(Integer.MIN_VALUE);
        if (code != 0) {
            log.warn("_parse_response code exception: NOWCODER code {}", code);
            throw new NowcoderApiException("NOWCODER message: " + body.path("msg").asText(), code);
        }
        return body.path("data");
    }
    private void downloadResultPdf(Exam exam) {
        // The Python implementation uploads to S3; this project has no equivalent file-upload service.
        if (exam.getResultUrl() == null || exam.getResultUrl().isBlank()) return;
        try {
            restClient.get().uri(URI.create(exam.getResultUrl())).retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> { })
                    .body(byte[].class);
        } catch (Exception e) {
            log.error("download_test_pdf: {}", e.getMessage());
        }
    }

    private boolean wasConcurrentlyFinalized(Exam exam) {
        List<Object[]> state = entityManager.createQuery(
                        "select e.dataStatus, e.status from Exam e where e.id = :id", Object[].class)
                .setParameter("id", exam.getId()).getResultList();
        if (state.isEmpty()) return false;
        Integer dataStatus = (Integer) state.get(0)[0];
        Integer status = (Integer) state.get(0)[1];
        return Integer.valueOf(DELETED_DATA_STATUS).equals(dataStatus)
                && (Integer.valueOf(4).equals(status) || Integer.valueOf(1).equals(status) || Integer.valueOf(5).equals(status));
    }
    private void saveUnlessChanged(Exam exam) {
        if (!wasConcurrentlyFinalized(exam)) examRepository.save(exam);
    }

    private void updateResumeStatus(Exam exam) {
        CandidateResume resume = candidateResumeRepository.findById(exam.getCandidateResumeId()).orElse(null);
        if (resume == null || !Integer.valueOf(3).equals(resume.getManagementStatus())) return;
        if (Integer.valueOf(DELETED_DATA_STATUS).equals(exam.getDataStatus())) return;
        if (Integer.valueOf(EXAM_SUBMITTED).equals(exam.getStatus()) || Integer.valueOf(EXAM_SYNCED).equals(exam.getStatus())) {
            resume.setManagementSubStatus(92);
            candidateResumeRepository.save(resume);
        }
    }
    private static String textOrNull(JsonNode node) {
        return node == null || node.isNull() ? null : node.asText();
    }
    private static Integer integerOrNull(JsonNode node) {
        return node == null || node.isNull() || !node.canConvertToInt() ? null : node.asInt();
    }
    private static Long longOrNull(JsonNode node) {
        return node == null || node.isNull() || !node.canConvertToLong() ? null : node.asLong();
    }
    private static Float floatOrNull(JsonNode node) {
        return node == null || node.isNull() || !node.isNumber() ? null : (float) node.asDouble();
    }
    private static final class NowcoderApiException extends RuntimeException {
        private final int code;
        private NowcoderApiException(String message, int code) { super(message); this.code = code; }
    }
}
