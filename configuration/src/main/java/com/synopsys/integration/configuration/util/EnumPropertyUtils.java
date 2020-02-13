package com.synopsys.integration.configuration.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;

public class EnumPropertyUtils {
    public static <T extends Enum<T>> List<String> getEnumNames(Class<T> enumClass) {
        final List<T> values = new ArrayList<>(EnumUtils.getEnumList(enumClass));
        return values.stream()
                   .map(Objects::toString)
                   .collect(Collectors.toList());
    }
}
