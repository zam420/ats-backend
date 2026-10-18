package com.deepconnect.ats.dto;

import com.deepconnect.ats.common.constant.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumsRsp {

    private Integer value;
    private String name;

    public static EnumsRsp from(BaseEnum e) {
        return EnumsRsp.builder()
                .value(e.getValue())
                .name(e.getName())
                .build();
    }
}
