package com.deepconnect.ats.candidate.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.deepconnect.ats.jobapplication.model.JobApplication;

@Data
public class CandidateResBase {

    protected String id;
    protected String firstName;
    protected String lastName;
    protected String preferredName;
    protected String email;
    protected String currentLocation;
    protected LocalDateTime createdAt;
    protected String rejectionReason;
    protected Boolean considerForOtherOpenings;
    protected LocalDateTime rejectedAt;
    protected String rejectedStageName;
    protected String rejectedStageType;

    protected CandidateResBase(JobApplication application) {
        this.id = application.getPublicId();
        this.firstName = application.getFirstName();
        this.lastName = application.getLastName();
        this.preferredName = application.getPreferredName();
        this.email = application.getEmail();
        this.currentLocation = application.getCurrentLocation();
        this.createdAt = application.getCreatedAt();
        this.rejectionReason = application.getRejectionReason();
        this.considerForOtherOpenings = application.getConsiderForOtherOpenings();
        this.rejectedAt = application.getRejectedAt();
        this.rejectedStageName = application.getRejectedStageName();
        this.rejectedStageType = application.getRejectedStageType();
    }
    
    public static CandidateResBase from(JobApplication application) {
        return new CandidateResBase(application);
    }

}
