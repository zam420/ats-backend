package com.deepconnect.ats.candidate.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.enums.JobApplicationCloseType;

@Getter 
@Setter 
public class CandidateClosureRes extends CandidateResBase {

    protected boolean closed;
    protected JobApplicationCloseType applicationCloseType;
    protected String closedReason;
    protected Boolean considerForOtherOpenings;
    protected LocalDateTime closedAt;
    protected String closedAtStageName;
    protected String closedAtStageType;

    protected CandidateClosureRes(JobApplication application) {
        super(application);
        this.closed = application.isClosed();
        this.applicationCloseType = application.getApplicationCloseType();
        this.closedReason = application.getClosedReason();
        this.considerForOtherOpenings = application.getConsiderForOtherOpenings();
        this.closedAt = application.getClosedAt();
        this.closedAtStageName = application.getClosedAtStageName();
        this.closedAtStageType = application.getClosedAtStageType();
    }
    
    public static CandidateClosureRes from(JobApplication application) {
        return new CandidateClosureRes(application);
    }

}
