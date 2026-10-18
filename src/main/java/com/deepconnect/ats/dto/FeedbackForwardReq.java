package com.deepconnect.ats.dto;

import lombok.Data;

@Data
public class FeedbackForwardReq {
    private Long forwardId;
    private Integer status;
    private String feedbackNotes;
}
