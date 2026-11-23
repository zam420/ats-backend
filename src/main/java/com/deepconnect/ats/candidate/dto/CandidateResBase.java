package com.deepconnect.ats.candidate.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.enums.JobApplicationCloseType;

@Data
public class CandidateResBase {

    protected String id;
    protected String firstName;
    protected String lastName;
    protected String preferredName;
    protected String email;
    protected String currentLocation;
    protected LocalDateTime createdAt;
    protected boolean closed;
    protected JobApplicationCloseType applicationCloseType;
    protected String closedReason;
    protected Boolean considerForOtherOpenings;
    protected LocalDateTime closedAt;
    protected String closedAtStageName;
    protected String closedAtStageType;

    protected CandidateResBase(JobApplication application) {
        this.id = application.getPublicId();
        this.firstName = application.getFirstName();
        this.lastName = application.getLastName();
        this.preferredName = application.getPreferredName();
        this.email = application.getEmail();
        this.currentLocation = application.getCurrentLocation();
        this.createdAt = application.getCreatedAt();
        this.closed = application.isClosed();
        this.applicationCloseType = application.getApplicationCloseType();
        this.closedReason = application.getClosedReason();
        this.considerForOtherOpenings = application.getConsiderForOtherOpenings();
        this.closedAt = application.getClosedAt();
        this.closedAtStageName = application.getClosedAtStageName();
        this.closedAtStageType = application.getClosedAtStageType();
    }
    
    public static CandidateResBase from(JobApplication application) {
        return new CandidateResBase(application);
    }

}
