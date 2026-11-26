package com.deepconnect.ats.candidatepipeline.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.candidatepipeline.dto.CandidateApplicationRes;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobScreeningQuestionRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.model.JobApplicationScreeningQuestionAnswer;
import com.deepconnect.ats.jobapplication.repo.JobApplicationAnswerRepository;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateApplicationService {
    private final JobPositionRepository positions;
    private final JobApplicationRepository applications;
    private final JobApplicationAnswerRepository answers;
    private final JobScreeningQuestionRepository questions;
    private final JobPositionAccessService access;

    @Value("${ats.storage.root:./uploads}")
    private String storageRoot;

    @Transactional(readOnly = true)
    public CandidateApplicationRes get(String jobId, String candidateId) {
        Context context = authorizedContext(jobId, candidateId);
        List<JobApplicationScreeningQuestionAnswer> records = answers.findByJobApplicationId(context.application().getId());
        Map<Long, JobScreeningQuestion> byId = questions.findByJobPositionIdOrderByIdAsc(context.job().getId()).stream()
                .collect(Collectors.toMap(JobScreeningQuestion::getId, Function.identity()));
        for (JobApplicationScreeningQuestionAnswer answer : records) {
            if (!byId.containsKey(answer.getJobScreeningQuestionId()) || answer.getAnswer() == null) {
                throw new InternalServerException("Application references an invalid screening question answer");
            }
        }
        List<CandidateApplicationRes.Answer> result = records.stream()
                .sorted(Comparator.comparing((JobApplicationScreeningQuestionAnswer answer) ->
                        byId.get(answer.getJobScreeningQuestionId()).getOrder())
                        .thenComparing(JobApplicationScreeningQuestionAnswer::getId))
                .map(answer -> new CandidateApplicationRes.Answer(
                        byId.get(answer.getJobScreeningQuestionId()).getLabel(), List.copyOf(answer.getAnswer())))
                .toList();
        return CandidateApplicationRes.from(context.application(), context.job().getTitle(), result);
    }

    @Transactional(readOnly = true)
    public Resume resume(String jobId, String candidateId) {
        JobApplication application = authorizedContext(jobId, candidateId).application();
        String stored = application.getResumePath();
        String filename = application.getResumeName();
        if (stored == null || !stored.matches("job-jobApplicationRepository/[0-9a-fA-F-]{36}\\.(pdf|doc|docx)")
                || filename == null || filename.isBlank() || filename.chars().anyMatch(Character::isISOControl)) {
            throw new InternalServerException("Invalid application resume metadata");
        }
        try {
            Path root = Paths.get(storageRoot).toRealPath();
            Path folder = root.resolve("job-jobApplicationRepository").toRealPath();
            Path path = root.resolve(stored).toRealPath();
            if (!folder.startsWith(root) || !path.getParent().equals(folder) || !Files.isRegularFile(path)) {
                throw new InternalServerException("Invalid application resume path");
            }
            return new Resume(path, filename);
        } catch (IOException e) {
            log.error("Failed to access application resume", e);
            throw new InternalServerException("Failed to access application resume");
        }
    }

    private Context authorizedContext(String jobId, String candidateId) {
        var user = AuthService.getLoggedInUser();
        JobPosition job = positions.findByPublicIdAndTenantIdAndDeactivatedFalse(jobId, user.getTenantId())
                .orElseThrow(() -> new InternalServerException("Job position not found"));
        access.assertReadAccess(user, job);
        JobApplication application = applications.findByPublicIdAndJobPositionId(candidateId, job.getId())
                .orElseThrow(() -> new CustomException(CustomExceptionType.JOB_PIPELINE_STALE));
        return new Context(job, application);
    }

    public record Resume(Path path, String filename) {}
    private record Context(JobPosition job, JobApplication application) {}
}
