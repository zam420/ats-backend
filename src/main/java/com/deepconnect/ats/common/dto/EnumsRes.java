package com.deepconnect.ats.common.dto;

import com.deepconnect.ats.common.utils.LocalizationUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EnumsRes {

    @JsonIgnore 
    private final LocalizationUtils localizationUtils;

    private Integer id;
    private String name;

}
