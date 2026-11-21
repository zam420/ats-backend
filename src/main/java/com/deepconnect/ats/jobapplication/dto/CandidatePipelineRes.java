package com.deepconnect.ats.jobapplication.dto;

import java.util.List;
import java.time.LocalDateTime;

public record CandidatePipelineRes(String jobId, String jobTitle, long totalApplications, List<Stage> stages) {
    public record Stage(String id, String name, String stageType, int order, List<Candidate> candidates) {}
    public record Candidate(String id, String firstName, String lastName, String preferredName,
            String email, String currentLocation, LocalDateTime createdAt) {}
    public record Job(String id, String title) {}
}
