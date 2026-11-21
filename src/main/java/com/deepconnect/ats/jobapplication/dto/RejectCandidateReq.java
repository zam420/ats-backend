package com.deepconnect.ats.jobapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RejectCandidateReq(@NotBlank String stageId, @Size(max = 2000) String reason,
        @NotNull Boolean considerForOtherOpenings) {}
