package com.deepconnect.ats.job.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.dto.JobApplicationReq;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.model.JobApplication;
import com.deepconnect.ats.job.model.JobApplicationScreeningQuestionAnswer;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.job.repo.JobApplicationAnswerRepository;
import com.deepconnect.ats.job.repo.JobApplicationRepository;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.screeningquestionsform.enums.ScreeningQuestionType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationService {
    private static final long MAX_RESUME_SIZE = 5L * 1024 * 1024;
    private final JobPositionRepository positions;
    private final JobApplicationRepository applications;
    private final JobApplicationAnswerRepository answers;

    @Value("${ats.storage.root:./uploads}")
    private String storageRoot;

    @Transactional
    public String submit(String publicId, JobApplicationReq request, MultipartFile resume) {
        JobPosition job = positions.findForApplication(publicId)
                .filter(position -> position.getStatus() == JobStatus.OPEN)
                .orElseThrow(() -> new CustomException(CustomExceptionType.JOB_APPLICATION_CLOSED));
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (applications.existsByJobPositionIdAndEmail(job.getId(), email)) {
            throw new CustomException(CustomExceptionType.JOB_APPLICATION_DUPLICATE);
        }
        List<JobApplicationScreeningQuestionAnswer> validated = validateAnswers(job, request.getAnswers());
        String originalName = resume == null ? "" : resume.getOriginalFilename();
        if (originalName == null) originalName = "";
        originalName = originalName.replace('\\', '/');
        originalName = originalName.substring(originalName.lastIndexOf('/') + 1);
        int dot = originalName.lastIndexOf('.');
        String extension = dot < 0 ? "" : originalName.substring(dot).toLowerCase(Locale.ROOT);
        if (resume == null || resume.isEmpty() || resume.getSize() > MAX_RESUME_SIZE
                || originalName.length() > 255 || !Set.of(".pdf", ".doc", ".docx").contains(extension)) {
            throw new CustomException(CustomExceptionType.JOB_APPLICATION_INVALID);
        }
        String storedName = UUID.randomUUID() + extension;
        Path root = Paths.get(storageRoot).toAbsolutePath().normalize();
        Path target = root.resolve("job-applications").resolve(storedName);
        try {
            Files.createDirectories(target.getParent());
            try (var input = resume.getInputStream()) {
                Files.copy(input, target);
            }
        } catch (IOException e) {
            try {
                Files.deleteIfExists(target);
            } catch (IOException cleanupError) {
                log.error("Failed to remove incomplete application resume", cleanupError);
            }
            throw new InternalServerException("Failed to store application resume");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    try {
                        Files.deleteIfExists(target);
                    } catch (IOException e) {
                        log.error("Failed to remove rolled-back application resume", e);
                    }
                }
            }
        });
        JobApplication application = new JobApplication();
        application.setJobPositionId(job.getId());
        application.setCandidateUserId(AuthService.getLoggedInUser().getUserId());
        application.setFirstName(request.getFirstName().trim());
        application.setLastName(request.getLastName().trim());
        application.setPreferredName(request.getPreferredName() == null ? null : request.getPreferredName().trim());
        application.setEmail(email);
        application.setCountryCode(request.getCountryCode().trim());
        application.setPhoneNumber(request.getPhoneNumber().trim());
        application.setCurrentLocation(request.getCurrentLocation().trim());
        application.setResumePath("job-applications/" + storedName);
        application.setResumeName(originalName);
        applications.saveAndFlush(application);
        for (JobApplicationScreeningQuestionAnswer answer : validated) {
            answer.setJobApplicationId(application.getId());
        }
        answers.saveAll(validated);
        return application.getPublicId();
    }

    private List<JobApplicationScreeningQuestionAnswer> validateAnswers(JobPosition job, List<JobApplicationReq.Answer> submitted) {
        List<JobScreeningQuestion> questions = job.getScreeningQuestions() == null ? List.of() : job.getScreeningQuestions();
        Map<String, JobApplicationReq.Answer> byQuestion = new HashMap<>();
        for (JobApplicationReq.Answer answer : submitted) {
            if (byQuestion.put(answer.getQuestionId(), answer) != null) {
                throw new CustomException(CustomExceptionType.JOB_APPLICATION_INVALID);
            }
        }
        if (byQuestion.size() != questions.size()) {
            throw new CustomException(CustomExceptionType.JOB_APPLICATION_STALE);
        }
        // Map<Integer, String> codes = new HashMap<>();
        // for (ScreeningQuestionType type : questionTypes.findAll()) codes.put(type.getId(), type.getCode());
        List<JobApplicationScreeningQuestionAnswer> result = new ArrayList<>();
        for (JobScreeningQuestion question : questions) {
            JobApplicationReq.Answer answer = byQuestion.get(question.getPublicId());
            if (answer == null) throw new CustomException(CustomExceptionType.JOB_APPLICATION_STALE);
            List<String> values = answer.getValues();
            if (question.getScreeningQuestionType() == ScreeningQuestionType.LABEL) {
                if (!values.isEmpty()) throw new CustomException(CustomExceptionType.JOB_APPLICATION_INVALID);
                continue;
            }
            boolean multi = question.getScreeningQuestionType() == ScreeningQuestionType.CHECKBOX
                    || (question.getScreeningQuestionType() == ScreeningQuestionType.DROPDOWN && Boolean.TRUE.equals(question.getIsDropDownMultiSelect()));
            boolean choice = question.getScreeningQuestionType() == ScreeningQuestionType.CHECKBOX
                    || question.getScreeningQuestionType() == ScreeningQuestionType.RADIO
                    || question.getScreeningQuestionType() == ScreeningQuestionType.DROPDOWN;
            if (!choice && question.getScreeningQuestionType() != ScreeningQuestionType.INPUT_BOX
                    && question.getScreeningQuestionType() != ScreeningQuestionType.INPUT_AREA) {
                throw new CustomException(CustomExceptionType.JOB_APPLICATION_STALE);
            }
            if ((Boolean.TRUE.equals(question.getIsMandatory()) && values.isEmpty())
                    || (!multi && values.size() > 1)
                    || new HashSet<>(values).size() != values.size()
                    || (choice && (question.getOptions() == null || !question.getOptions().containsAll(values)))
                    || (!choice && question.getMaxChars() != null && values.stream().anyMatch(value -> value.length() > question.getMaxChars()))) {
                throw new CustomException(CustomExceptionType.JOB_APPLICATION_INVALID);
            }
            JobApplicationScreeningQuestionAnswer record = new JobApplicationScreeningQuestionAnswer();
            record.setJobScreeningQuestionId(question.getId());
            record.setAnswer(List.copyOf(values));
            result.add(record);
        }
        return result;
    }
}
