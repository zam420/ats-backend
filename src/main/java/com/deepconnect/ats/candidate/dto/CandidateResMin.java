package com.deepconnect.ats.candidate.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import com.deepconnect.ats.jobapplication.enums.JobApplicationCloseType;
import com.deepconnect.ats.jobapplication.model.JobApplication;

@Getter
@Setter
public class CandidateResMin {

    protected String id;
    protected String name;
    protected JobApplicationCloseType applicationCloseType;

    protected CandidateResMin(JobApplication application) {
        this.id = application.getPublicId();
        String preferredName = application.getPreferredName();
        this.name = preferredName == null || preferredName.isEmpty()
                ? application.getName() : preferredName + " " + application.getLastName();
        this.applicationCloseType = application.isClosed() ? application.getApplicationCloseType() : null;
    }
    
    public static CandidateResMin from(JobApplication application) {
        return new CandidateResMin(application);
    }

    public static List<CandidateResMin> from(List<JobApplication> entities) {
        return entities.stream().map(CandidateResMin::from).toList();
    }
}
