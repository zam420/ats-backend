package com.deepconnect.ats.dto;

import lombok.Data;

@Data
public class ChangeUserStatusReq {
    private Long id;
    private Integer status;
}
