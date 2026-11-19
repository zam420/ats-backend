package com.deepconnect.ats.common.utils;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;

import org.apache.poi.ss.formula.functions.T;

import com.deepconnect.ats.common.dto.EnumsRes;
import com.deepconnect.ats.common.enums.LocalizedBaseEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class EnumsResUtils {

    private final LocalizationUtils localizationUtils;

    public EnumsRes from(LocalizedBaseEnum e) {
        String message = localizationUtils.getLocalizedString(e.getKey());
        return EnumsRes.builder()
                .id(e.getId())
                .name(message)
                .build();
    }

    public <T extends LocalizedBaseEnum> List<EnumsRes> from(T[] values) {
        return Arrays.stream(values)
                .map(this::from)
                .collect(Collectors.toList());
    }

}
