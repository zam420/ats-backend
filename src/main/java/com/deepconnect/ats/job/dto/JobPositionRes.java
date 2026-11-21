package com.deepconnect.ats.job.dto;

import lombok.Data;
import java.util.List;
import org.springframework.util.CollectionUtils;
import com.deepconnect.ats.job.enums.JobEmploymentType;
import com.deepconnect.ats.job.enums.JobWorkArrangementType;
import com.deepconnect.ats.job.enums.JobExperienceLevelType;

import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.job.security.JobRichTextSanitizer;
import com.deepconnect.ats.job.enums.JobQuestionsDisplayMode;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.screeningquestionsform.enums.ScreeningQuestionType;

@Data
public class JobPositionRes {

    private String id;
    private String title;
    private String description;
    private JobEmploymentType employmentType;
    private JobWorkArrangementType workArrangementType;
    private JobExperienceLevelType experienceLevelType;
    private List<Integer> workLocation;
    private String requisitionId;
    private Integer numOfOpenings;
    private String office;
    private String department;
    private List<String> recruiters;
    private List<String> hiringManagers;
    private JobQuestionsDisplayMode applicationQuestionsDisplayMode;
    private List<ScreeningQuestion> screeningQuestions;
    private List<RecruitmentRound> recruitmentPipelineRounds;
    private List<EvaluationRound> evaluationPlanRounds;
    private JobStatus status;
    private String ownerId;
    private long applicationCount;

    public record ScreeningQuestion(Long id, String publicId, Long jobPositionId, String label,
            String description, List<String> options, String defaultOption, Boolean isMandatory,
            Boolean isDropDownMultiSelect, Integer maxChars, Integer headingLevel,
            Integer screeningQuestionTypeId, Integer order) {
        static ScreeningQuestion from(JobScreeningQuestion question) {
            return new ScreeningQuestion(question.getId(), question.getPublicId(), question.getJobPositionId(),
                question.getLabel(), question.getDescription(), question.getOptions(), question.getDefaultOption(),
                question.getIsMandatory(), question.getIsDropDownMultiSelect(), question.getMaxChars(),
                question.getHeadingLevel(), question.getScreeningQuestionType().getId(), question.getOrder());
        }
    }

    public record RecruitmentRound(Long id, String publicId, Long jobPositionId, String name,
            String description, Integer roundType, Integer order) {
        static RecruitmentRound from(JobRecruitmentPipelineRound round) {
            return new RecruitmentRound(round.getId(), round.getPublicId(), round.getJobPositionId(),
                round.getName(), round.getDescription(), round.getRoundType().getId(), round.getOrder());
        }
    }

    public record EvaluationRound(Long id, String publicId, Long jobPositionId, String name,
            String description, Integer roundType, List<String> interviewers, Integer order) {
        static EvaluationRound from(JobEvaluationPlanRound round) {
            return new EvaluationRound(round.getId(), round.getPublicId(), round.getJobPositionId(),
                round.getName(), round.getDescription(), round.getRoundType().getId(), round.getInterviewers(), round.getOrder());
        }
    }
    
    public static JobPositionRes from(JobPosition entity) {
        JobPositionRes res = new JobPositionRes();
        res.setId(entity.getPublicId());
        res.setTitle(entity.getTitle());
        res.setDescription(JobRichTextSanitizer.sanitize(entity.getDescription()));
        res.setEmploymentType(entity.getEmploymentType());
        res.setWorkArrangementType(entity.getWorkArrangementType());
        res.setExperienceLevelType(entity.getExperienceLevelType());
        res.setWorkLocation(entity.getWorkLocation());
        res.setRequisitionId(entity.getRequisitionId());
        res.setNumOfOpenings(entity.getNumOfOpenings());
        res.setOffice(entity.getOffice());
        res.setDepartment(entity.getDepartment());
        res.setRecruiters(entity.getRecruiters());
        res.setHiringManagers(entity.getHiringManagers());
        res.setApplicationQuestionsDisplayMode(entity.getApplicationQuestionsDisplayMode());
        if(!CollectionUtils.isEmpty(entity.getScreeningQuestions())) {
            res.setScreeningQuestions(entity.getScreeningQuestions().stream().map(ScreeningQuestion::from).toList());
        }
        if(!CollectionUtils.isEmpty(entity.getEvaluationPlanRounds())) {
            res.setEvaluationPlanRounds(entity.getEvaluationPlanRounds().stream().map(EvaluationRound::from).toList());
        }
        if(!CollectionUtils.isEmpty(entity.getRecruitmentPipelineRounds())) {
            res.setRecruitmentPipelineRounds(entity.getRecruitmentPipelineRounds().stream().map(RecruitmentRound::from).toList());
        }
        res.setStatus(entity.getStatus());
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<JobPositionRes> fromList(List<JobPosition> entities) {
        return entities.stream().map(JobPositionRes::from).toList();
    }
}
