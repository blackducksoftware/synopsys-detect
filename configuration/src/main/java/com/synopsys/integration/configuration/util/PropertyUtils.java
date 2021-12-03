package com.synopsys.integration.configuration.util;

import java.util.List;
import java.util.stream.Collectors;

public class PropertyUtils {
    public static <T> String describeObjectList(List<T> objects) {
        return objects.stream()
            .map(Object::toString)
            .collect(Collectors.joining(","));
    }
}
