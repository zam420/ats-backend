package com.deepconnect.ats.job.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import org.springframework.util.CollectionUtils;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionReqRes;
import com.deepconnect.ats.job.enums.JobQuestionsDisplayMode;

@Getter
@Setter
public class JobPositionCandidateRes extends JobPositionResBase {

    private String companyName;
    private String companyId;
    private boolean companyHasLogo;
    private String companyLogoUrl;
    private JobQuestionsDisplayMode applicationQuestionsDisplayMode;
    private List<ScreeningQuestionReqRes> screeningQuestions;

    protected JobPositionCandidateRes(JobPosition entity) {
        super(entity);
    }
    
    public static JobPositionCandidateRes from(JobPosition entity) {
        JobPositionCandidateRes res = new JobPositionCandidateRes(entity);
        res.setCompanyName(entity.getTenant().getName());
        res.setCompanyId(entity.getTenant().getPublicId());
        res.setCompanyHasLogo(entity.getTenant().getLogoPath() != null);
        res.setCompanyLogoUrl(entity.getTenant().getLogoPath());
        res.setApplicationQuestionsDisplayMode(entity.getApplicationQuestionsDisplayMode());
        if(!CollectionUtils.isEmpty(entity.getScreeningQuestions())) {
            res.setScreeningQuestions(entity.getScreeningQuestions().stream().map(ScreeningQuestionReqRes::from).toList());
        }
        return res;
    }

    public static List<JobPositionCandidateRes> fromList(List<JobPosition> entities) {
        return entities.stream().map(JobPositionCandidateRes::from).toList();
    }
}
