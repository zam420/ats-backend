package com.deepconnect.ats.job.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.enums.JobQuestionsDisplayMode;
import com.deepconnect.ats.job.enums.JobStatus;

@Getter
@Setter
public class JobPositionRes extends JobPositionResBase {

    private List<String> recruiters;
    private List<String> hiringManagers;
    private JobQuestionsDisplayMode applicationQuestionsDisplayMode;
    private JobStatus status;
    private String ownerId;
    private String ownerName;
    private long applicationCount;

    protected JobPositionRes(JobPosition entity) {
        super(entity);
    }
    
    public static JobPositionRes from(JobPosition entity) {
        JobPositionRes res = new JobPositionRes(entity);
        res.setRecruiters(entity.getRecruiters());
        res.setHiringManagers(entity.getHiringManagers());
        res.setApplicationQuestionsDisplayMode(entity.getApplicationQuestionsDisplayMode());
        res.setStatus(entity.getStatus());
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<JobPositionRes> fromList(List<JobPosition> entities) {
        return entities.stream().map(JobPositionRes::from).toList();
    }
}
