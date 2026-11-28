package com.deepconnect.ats.candidatepipeline.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.deepconnect.ats.jobapplication.model.JobApplication;

public record CandidateApplicationRes(String id, String jobId, String jobTitle, String firstName, String lastName,
        String preferredName, String email, String countryCode, String phoneNumber, String currentLocation,
        String resumeName, LocalDateTime createdAt, List<Answer> answers) {
    public record Answer(String question, List<String> values) {}

    public static CandidateApplicationRes from(JobApplication application, String jobId, String jobTitle, List<Answer> answers) {
        return new CandidateApplicationRes(application.getPublicId(), jobId, jobTitle, application.getFirstName(),
                application.getLastName(), application.getPreferredName(), application.getEmail(),
                application.getCountryCode(), application.getPhoneNumber(), application.getCurrentLocation(),
                application.getResumeName(), application.getCreatedAt(), answers);
    }
}
