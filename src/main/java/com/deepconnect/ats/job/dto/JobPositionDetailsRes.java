package com.deepconnect.ats.job.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import org.springframework.util.CollectionUtils;

import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRoundReqRes;

import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRoundReqRes;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionReqRes;
import com.deepconnect.ats.job.enums.JobQuestionsDisplayMode;
import com.deepconnect.ats.job.enums.JobStatus;

@Getter
@Setter
public class JobPositionDetailsRes extends JobPositionResBase {

    private List<String> recruiters;
    private List<String> hiringManagers;
    private JobQuestionsDisplayMode applicationQuestionsDisplayMode;
    private List<ScreeningQuestionReqRes> screeningQuestions;
    private List<RecruitmentPipelineRoundReqRes> recruitmentPipelineRounds;
    private List<EvaluationPlanRoundReqRes> evaluationPlanRounds;
    private JobStatus status;
    private String ownerId;
    private String ownerName;
    private long applicationCount;

    protected JobPositionDetailsRes(JobPosition entity) {
        super(entity);
    }
    
    public static JobPositionDetailsRes from(JobPosition entity) {
        JobPositionDetailsRes res = new JobPositionDetailsRes(entity);
        res.setRecruiters(entity.getRecruiters());
        res.setHiringManagers(entity.getHiringManagers());
        res.setApplicationQuestionsDisplayMode(entity.getApplicationQuestionsDisplayMode());
        if(!CollectionUtils.isEmpty(entity.getScreeningQuestions())) {
            res.setScreeningQuestions(entity.getScreeningQuestions().stream().map(ScreeningQuestionReqRes::from).toList());
        }
        if(!CollectionUtils.isEmpty(entity.getEvaluationPlanRounds())) {
            res.setEvaluationPlanRounds(entity.getEvaluationPlanRounds().stream().map(EvaluationPlanRoundReqRes::from).toList());
        }
        if(!CollectionUtils.isEmpty(entity.getRecruitmentPipelineRounds())) {
            res.setRecruitmentPipelineRounds(entity.getRecruitmentPipelineRounds().stream().map(RecruitmentPipelineRoundReqRes::from).toList());
        }
        res.setStatus(entity.getStatus());
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<JobPositionDetailsRes> fromList(List<JobPosition> entities) {
        return entities.stream().map(JobPositionDetailsRes::from).toList();
    }
}
