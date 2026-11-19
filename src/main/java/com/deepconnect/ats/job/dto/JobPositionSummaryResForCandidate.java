package com.deepconnect.ats.job.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.security.JobRichTextSanitizer;

@Data
public class JobPositionSummaryResForCandidate {

    private String id;
    private String title;
    private String companyName;
    private boolean companyHasLogo;
    private LocalDateTime publishedAt;
    private String shortDescription;
    private Integer employmentType;
    private Integer workArrangementType;
    private Integer experienceLevelType;
    private List<Integer> workLocation;
    
    public static JobPositionSummaryResForCandidate from(JobPosition entity) {
        JobPositionSummaryResForCandidate res = new JobPositionSummaryResForCandidate();
        res.setId(entity.getPublicId());
        res.setTitle(entity.getTitle());
        res.setCompanyName(entity.getTenant().getName());
        res.setCompanyHasLogo(entity.getTenant().getLogoPath() != null);
        res.setPublishedAt(entity.getPublishedAt());
        res.setShortDescription(entity.getShortDescription());
        res.setEmploymentType(entity.getEmploymentTypeId());
        res.setWorkArrangementType(entity.getWorkArrangementTypeId());
        res.setExperienceLevelType(entity.getExperienceLevelTypeId());
        res.setWorkLocation(entity.getWorkLocation());
        return res;
    }

    public static List<JobPositionSummaryResForCandidate> fromList(List<JobPosition> entities) {
        return entities.stream().map(JobPositionSummaryResForCandidate::from).toList();
    }

    public static JobPositionSummaryResForCandidate from(JobPositionResForCandidate entity) {
        JobPositionSummaryResForCandidate res = new JobPositionSummaryResForCandidate();
        res.setId(entity.getId());
        res.setTitle(entity.getTitle());
        res.setCompanyName(entity.getCompanyName());
        res.setCompanyHasLogo(entity.isCompanyHasLogo());
        res.setPublishedAt(entity.getPublishedAt());
        res.setShortDescription(JobRichTextSanitizer.shortDescription(entity.getDescription()));
        res.setEmploymentType(entity.getEmploymentType());
        res.setWorkArrangementType(entity.getWorkArrangementType());
        res.setExperienceLevelType(entity.getExperienceLevelType());
        res.setWorkLocation(entity.getWorkLocation());
        return res;
    }

    public static List<JobPositionSummaryResForCandidate> fromListForCandidate(List<JobPositionResForCandidate> entities) {
        return entities.stream().map(JobPositionSummaryResForCandidate::from).toList();
    }

    
}
