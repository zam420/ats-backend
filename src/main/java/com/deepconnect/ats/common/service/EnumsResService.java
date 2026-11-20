package com.deepconnect.ats.common.service;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;

import com.deepconnect.ats.common.dto.EnumsExtendedRes;
import com.deepconnect.ats.common.dto.EnumsRes;
import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.deepconnect.ats.common.enums.LocalizedBaseExtendedEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class EnumsResService {

    private final LocalizationService localizationService;

    public EnumsRes from(LocalizedBaseEnum e) {
        String name = localizationService.getLocalizedString(e.getKey());
        return EnumsRes.builder()
                .id(e.getId())
                .name(name)
                .build();
    }

    public EnumsExtendedRes from(LocalizedBaseExtendedEnum e) {
        String name = localizationService.getLocalizedString(e.getKey());
        String description = localizationService.getLocalizedString(e.getKey() + ".description");
        return EnumsExtendedRes.builder()
                .id(e.getId())
                .name(name)
                .code(e.getCode())
                .description(description)
                .build();
    }

    public <T extends LocalizedBaseEnum> List<EnumsRes> from(T[] values) {
        return Arrays.stream(values)
                .map(this::from)
                .collect(Collectors.toList());
    }

    public <T extends LocalizedBaseExtendedEnum> List<EnumsExtendedRes> from(T[] values) {
        return Arrays.stream(values)
                .map(this::from)
                .collect(Collectors.toList());
    }

    public <T extends LocalizedBaseEnum> T resolve(Integer id, T[] values) {
        if (id == null) {
            return null;
        }
        for (T mode : values) {
            if (mode.getId().equals(id)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }

}
