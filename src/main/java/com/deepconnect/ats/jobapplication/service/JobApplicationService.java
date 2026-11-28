package com.deepconnect.ats.jobapplication.service;

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
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobPositionSearchSpecification;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.jobapplication.dto.JobApplicationReq;
import com.deepconnect.ats.jobapplication.dto.JobApplicationSearchQuery;
import com.deepconnect.ats.jobapplication.dto.JobApplicationSummaryRes;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.model.JobApplicationScreeningQuestionAnswer;
import com.deepconnect.ats.jobapplication.repo.JobApplicationAnswerRepository;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.screeningquestionsform.enums.ScreeningQuestionType;
import com.deepconnect.ats.dto.AtsPageRsp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationService {
    private final JobPositionRepository jobPositionRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobApplicationAnswerRepository jobApplicationAnswerRepository;
    private final JobRecruitmentPipelineRoundRepository jobRecruitmentPipelineRoundRepository;

    @Value("${ats.storage.root:./uploads}")
    private String storageRoot;

    @Value("${jobApplication.maxResumeSizeBytes}")
    private long maxResumeSizeBytes;

    @Value("${ats.logo.public-base-url:}")
    private String publicLogoBaseUrl;

    @Transactional(readOnly = true)
    public AtsPageRsp<JobApplicationSummaryRes> findAllForCandidate(JobApplicationSearchQuery query) {
        Long candidateUserId = AuthService.getLoggedInUser().getUserId();
        return AtsPageRsp.of(jobApplicationRepository.findAll(
                JobPositionSearchSpecification.forApplications(query, candidateUserId), query.toPageable())
                .map(application -> new JobApplicationSummaryRes(application, publicLogoBaseUrl)));
    }

    @Transactional
    public String submit(String publicId, JobApplicationReq request, MultipartFile resume) {
        JobPosition job = jobPositionRepository.findForApplication(publicId)
                .filter(position -> position.getStatus() == JobStatus.OPEN)
                .orElseThrow(() -> new CustomException(CustomExceptionType.JOB_APPLICATION_CLOSED));
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (jobApplicationRepository.existsByJobPositionIdAndEmail(job.getId(), email)) {
            throw new CustomException(CustomExceptionType.JOB_APPLICATION_DUPLICATE);
        }
        List<JobRecruitmentPipelineRound> rounds = jobRecruitmentPipelineRoundRepository.findByJobPositionIdOrderByOrderAsc(job.getId());
        if (rounds.isEmpty() || rounds.get(0).getRoundType() != RecruitmentPipelineRoundType.APPLIED) {
            throw new CustomException(CustomExceptionType.JOB_PIPELINE_INVALID);
        }
        var plan = job.getEvaluationPlanRounds();
        if (plan == null || plan.isEmpty() || plan.get(0) == null || plan.get(0).getPlanVersion() == null
                || plan.get(0).getPlanVersion() < 1
                || plan.stream().anyMatch(round -> round == null || !plan.get(0).getPlanVersion().equals(round.getPlanVersion()))) {
            throw new InternalServerException("Cannot apply to a job without a valid versioned evaluation plan");
        }
        List<JobApplicationScreeningQuestionAnswer> validated = validateAnswers(job, request.getAnswers());
        String originalName = resume == null ? "" : resume.getOriginalFilename();
        if (originalName == null) originalName = "";
        originalName = originalName.replace('\\', '/');
        originalName = originalName.substring(originalName.lastIndexOf('/') + 1);
        int dot = originalName.lastIndexOf('.');
        String extension = dot < 0 ? "" : originalName.substring(dot).toLowerCase(Locale.ROOT);
        if (resume == null || resume.isEmpty() || resume.getSize() > maxResumeSizeBytes
                || originalName.length() > 255 || !Set.of(".pdf", ".doc", ".docx").contains(extension)) {
            throw new CustomException(CustomExceptionType.JOB_APPLICATION_INVALID);
        }
        String storedName = UUID.randomUUID() + extension;
        Path root = Paths.get(storageRoot).toAbsolutePath().normalize();
        Path target = root.resolve("job-jobApplicationRepository").resolve(storedName);
        try {
            Files.createDirectories(target.getParent());
            try (var input = resume.getInputStream()) {
                Files.copy(input, target);
            }
        } catch (IOException e) {// Best-effort cleanup so a partially written or empty file isn't left behind on failure
            try {
                Files.deleteIfExists(target);
            } catch (IOException cleanupError) {
                log.error("Failed to remove incomplete application resume", cleanupError);
            }
            throw new InternalServerException("Failed to store application resume");
        }

        // Tie disk file cleanup to the enclosing Spring database transaction lifecycle so orphaned files
        // are deleted if the database transaction rolls back or fails to commit
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
        application.setJobPositionPublicId(job.getPublicId());
        application.setEvaluationPlanVersion(plan.get(0).getPlanVersion());
        application.setRecruitmentPipelineStage(rounds.get(0).getId());
        application.setCandidateUserId(AuthService.getLoggedInUser().getUserId());
        application.setFirstName(request.getFirstName().trim());
        application.setLastName(request.getLastName().trim());
        application.setPreferredName(request.getPreferredName() == null ? null : request.getPreferredName().trim());
        application.setEmail(email);
        application.setCountryCode(request.getCountryCode().trim());
        application.setPhoneNumber(request.getPhoneNumber().trim());
        application.setCurrentLocation(request.getCurrentLocation().trim());
        application.setResumePath("job-jobApplicationRepository/" + storedName);
        application.setResumeName(originalName);
        jobApplicationRepository.saveAndFlush(application);
        for (JobApplicationScreeningQuestionAnswer answer : validated) {
            answer.setJobApplicationId(application.getId());
        }
        jobApplicationAnswerRepository.saveAll(validated);
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

    public boolean existsByRecruitmentPipelineStageAndClosedFalse(Long stageId) {
        return jobApplicationRepository.existsByRecruitmentPipelineStageAndClosedFalse(stageId);
    }

    /**
     * Clears the closed stages for the given list of stage IDs.
     * If a job position owner deletes the recruitment pipeline stage, 
     * when some of the job applications in closed (rejected/offer cancelled/not joined) state are associated with the deleted stages, 
     * this method will set recruitmentPipelineStage to null for such stages from the job applications. This will not impact the display of the application's closed stage
     * since the closed stage information is maintained separately in closedAtStageName and closedAtStageType fields.
     */
    public void clearClosedStages(List<Long> stageIds) {
        jobApplicationRepository.clearClosedStages(stageIds);
    }
}
