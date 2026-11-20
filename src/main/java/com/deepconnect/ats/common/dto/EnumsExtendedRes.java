package com.deepconnect.ats.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EnumsExtendedRes {

    private Integer id;
    private String name;
    private String code;
    private String description;

}
