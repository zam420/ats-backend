package com.deepconnect.ats.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class SaveUserReq {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Integer type;
    private Integer status;
    private List<Long> roleIds;
}
