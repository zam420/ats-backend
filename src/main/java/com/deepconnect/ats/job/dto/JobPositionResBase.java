package com.deepconnect.ats.job.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.security.JobRichTextSanitizer;
import com.deepconnect.ats.job.enums.JobEmploymentType;
import com.deepconnect.ats.job.enums.JobWorkArrangementType;
import com.deepconnect.ats.job.enums.JobExperienceLevelType;

@Data
public class JobPositionResBase {

    protected String id;
    protected String title;
    protected String description;
    protected JobEmploymentType employmentType;
    protected JobWorkArrangementType workArrangementType;
    protected JobExperienceLevelType experienceLevelType;
    protected List<Integer> workLocation;
    protected String requisitionId;
    protected Integer numOfOpenings;
    protected String office;
    protected String department;
    protected LocalDateTime publishedAt;

    protected JobPositionResBase(JobPosition entity) {
        this.id = entity.getPublicId();
        this.title = entity.getTitle();
        this.description = JobRichTextSanitizer.sanitize(entity.getDescription());
        this.employmentType = entity.getEmploymentType();
        this.workArrangementType = entity.getWorkArrangementType();
        this.experienceLevelType = entity.getExperienceLevelType();
        this.workLocation = entity.getWorkLocation();
        this.requisitionId = entity.getRequisitionId();
        this.numOfOpenings = entity.getNumOfOpenings();
        this.office = entity.getOffice();
        this.department = entity.getDepartment();
        this.publishedAt = entity.getPublishedAt();
    }
    
    protected static JobPositionResBase from(JobPosition entity) {
        return new JobPositionResBase(entity);
    }

}
