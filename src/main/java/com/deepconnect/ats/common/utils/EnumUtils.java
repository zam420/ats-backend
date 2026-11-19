package com.deepconnect.ats.common.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumUtils {

    public static <EnumType extends Enum<EnumType>> HashMap<Integer, String> EnumToHash(Class<EnumType> enumType) {
        EnumSet<EnumType> allEnumValues = EnumSet.allOf(enumType);
        return EnumToHash(enumType, allEnumValues);
    }

    public static <EnumType extends Enum<EnumType>> HashMap<Integer, String> EnumToHash(Class<EnumType> enumType,
            Set<EnumType> enumValues) {
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        for (EnumType s : enumValues) {
            result.put(s.ordinal(), s.name());
        }
        return result;
    }

    public static <EnumType extends Enum<EnumType>> Set<Integer> getOrdinalsFromSet(Set<EnumType> input) {
        return input.stream().map(EnumType::ordinal).collect(Collectors.toSet());
    }

    public static <EnumType extends Enum<EnumType>> List<Integer> getOrdinalsFromList(List<EnumType> input) {
        return input.stream().map(EnumType::ordinal).collect(Collectors.toList());
    }


}
