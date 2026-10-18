package com.deepconnect.ats.dto;

import lombok.Data;

@Data
public class SaveTalentReq {
    private Long id;
    private Long candidateResumeId;
    private Integer pooledReason;
    private String comments;
}
