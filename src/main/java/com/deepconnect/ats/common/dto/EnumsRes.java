package com.deepconnect.ats.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EnumsRes {

    private Integer id;
    private String name;

}
