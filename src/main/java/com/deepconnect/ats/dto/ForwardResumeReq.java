package com.deepconnect.ats.dto;

import lombok.Data;

import java.util.List;

@Data
public class ForwardResumeReq {
    private List<Long> resumeIds;
    private List<Long> toUserIds;
    private Long positionId;
    private String forwardNotes;
}
