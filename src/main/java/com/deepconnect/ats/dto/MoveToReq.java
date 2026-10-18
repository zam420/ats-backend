package com.deepconnect.ats.dto;

import lombok.Data;

import java.util.List;

@Data
public class MoveToReq {
    private List<Long> resumeIds;
    private Integer targetStatus;
    private Integer targetSubStatus;
    private Long targetPositionId;
}
