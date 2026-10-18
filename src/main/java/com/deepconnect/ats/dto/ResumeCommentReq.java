package com.deepconnect.ats.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResumeCommentReq {
    private Long candidateResumeId;
    private List<Long> candidateResumeIds;
    private String comment;
    private List<Long> shareTo;
}
