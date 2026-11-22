package com.deepconnect.ats.candidatepipeline.dto;

import jakarta.validation.constraints.NotBlank;

public record MoveCandidateReq(@NotBlank String fromStageId, @NotBlank String toStageId) {}
