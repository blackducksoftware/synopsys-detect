package com.synopsys.integration.detect.util;

import java.util.Optional;

public class DetectEnumUtil {
    public static <T extends Enum<T>> Optional<T> getValueOf(Class<T> enumType, String name) {
        try {
            return Optional.of(Enum.valueOf(enumType, name));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}

