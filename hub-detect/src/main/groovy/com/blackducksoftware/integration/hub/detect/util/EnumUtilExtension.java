package com.blackducksoftware.integration.hub.detect.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumUtilExtension {
    //TODO: Replace with EnumUtil inside of 40.1 of Hub Common
    public static <T extends Enum<T>> List<T> parseCommaDelimitted(String commaDelimittedEnumString, Class<T> enumClass) {
        return Arrays.stream(commaDelimittedEnumString.split("\\s*,\\s*"))
                   .map(token -> Enum.valueOf(enumClass, token))
                   .collect(Collectors.toList());
    }
}
