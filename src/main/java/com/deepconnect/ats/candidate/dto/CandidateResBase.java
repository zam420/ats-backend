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

    protected CandidateResBase(JobApplication application) {
        this.id = application.getPublicId();
        this.firstName = application.getFirstName();
        this.lastName = application.getLastName();
        this.preferredName = application.getPreferredName();
        this.email = application.getEmail();
        this.currentLocation = application.getCurrentLocation();
        this.createdAt = application.getCreatedAt();
    }
    
    public static CandidateResBase from(JobApplication application) {
        return new CandidateResBase(application);
    }

}
