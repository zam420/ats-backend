package com.deepconnect.ats.dto;

import lombok.Data;

@Data
public class OfferReq {
    private Long id;
    private Long candidateResumeId;
    private Integer feedbackStatus;
}
