package com.deepconnect.ats.job.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.util.CollectionUtils;

import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.job.security.JobRichTextSanitizer;
import com.deepconnect.ats.job.enums.JobQuestionsDisplayMode;

@Data
public class JobPositionResForCandidate {

    private String id;
    private String title;
    private String companyName;
    private boolean companyHasLogo;
    private String companyLogoUrl;
    private LocalDateTime publishedAt;
    private String description;
    private Integer employmentType;
    private Integer workArrangementType;
    private Integer experienceLevelType;
    private List<Integer> workLocation;
    private String requisitionId;
    private Integer numOfOpenings;
    private String office;
    private String department;
    private JobQuestionsDisplayMode applicationQuestionsDisplayMode;
    private List<ScreeningQuestion> screeningQuestions;

    public record ScreeningQuestion(Long id, String publicId, Long jobPositionId, String label,
            String description, List<String> options, String defaultOption, Boolean isMandatory,
            Boolean isDropDownMultiSelect, Integer maxChars, Integer headingLevel,
            Integer screeningQuestionTypeId, Integer order) {
        static ScreeningQuestion from(JobScreeningQuestion question) {
            return new ScreeningQuestion(question.getId(), question.getPublicId(), question.getJobPositionId(),
                question.getLabel(), question.getDescription(), question.getOptions(), question.getDefaultOption(),
                question.getIsMandatory(), question.getIsDropDownMultiSelect(), question.getMaxChars(),
                question.getHeadingLevel(), question.getScreeningQuestionTypeId(), question.getOrder());
        }
    }

    public record RecruitmentRound(Long id, String publicId, Long jobPositionId, String name,
            String description, Integer roundType, Integer order) {
        static RecruitmentRound from(JobRecruitmentPipelineRound round) {
            return new RecruitmentRound(round.getId(), round.getPublicId(), round.getJobPositionId(),
                round.getName(), round.getDescription(), round.getRoundType(), round.getOrder());
        }
    }

    public record EvaluationRound(Long id, String publicId, Long jobPositionId, String name,
            String description, Integer roundType, List<String> interviewers, Integer order) {
        static EvaluationRound from(JobEvaluationPlanRound round) {
            return new EvaluationRound(round.getId(), round.getPublicId(), round.getJobPositionId(),
                round.getName(), round.getDescription(), round.getRoundType(), round.getInterviewers(), round.getOrder());
        }
    }
    
    public static JobPositionResForCandidate from(JobPosition entity) {
        JobPositionResForCandidate res = new JobPositionResForCandidate();
        res.setId(entity.getPublicId());
        res.setTitle(entity.getTitle());
        res.setCompanyName(entity.getTenant().getName());
        res.setCompanyHasLogo(entity.getTenant().getLogoPath() != null);
        res.setPublishedAt(entity.getPublishedAt());
        res.setDescription(JobRichTextSanitizer.sanitize(entity.getDescription()));
        res.setEmploymentType(entity.getEmploymentTypeId());
        res.setWorkArrangementType(entity.getWorkArrangementTypeId());
        res.setExperienceLevelType(entity.getExperienceLevelTypeId());
        res.setWorkLocation(entity.getWorkLocation());
        res.setRequisitionId(entity.getRequisitionId());
        res.setNumOfOpenings(entity.getNumOfOpenings());
        res.setOffice(entity.getOffice());
        res.setDepartment(entity.getDepartment());
        res.setApplicationQuestionsDisplayMode(entity.getApplicationQuestionsDisplayMode());
        if(!CollectionUtils.isEmpty(entity.getScreeningQuestions())) {
            res.setScreeningQuestions(entity.getScreeningQuestions().stream().map(ScreeningQuestion::from).toList());
        }
        return res;
    }

    public static List<JobPositionResForCandidate> fromList(List<JobPosition> entities) {
        return entities.stream().map(JobPositionResForCandidate::from).toList();
    }
}
