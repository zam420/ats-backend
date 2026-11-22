package com.deepconnect.ats.candidatepipeline.dto;

import java.util.List;
import java.time.LocalDateTime;

public record CandidatePipelineRes(String jobId, String jobTitle, long totalApplications, long pipelineCount,
        List<Stage> stages, List<Candidate> rejectedCandidates) {
    public record Stage(String id, String name, String stageType, int order, List<Candidate> candidates) {}
    public record Candidate(String id, String firstName, String lastName, String preferredName,
            String email, String currentLocation, LocalDateTime createdAt,
            String rejectionReason, Boolean considerForOtherOpenings, LocalDateTime rejectedAt,
            String rejectedStageName, String rejectedStageType) {}
    public record Job(String id, String title) {}
}
