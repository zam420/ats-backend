package com.deepconnect.ats.jobapplication.dto;

import jakarta.validation.constraints.NotBlank;

public record MoveCandidateReq(@NotBlank String fromStageId, @NotBlank String toStageId) {}
