/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;

public class EnumPropertyUtils {

    public static <T extends Enum<T>> List<String> getEnumNamesAnd(Class<T> enumClass, String... additional) {
        List<String> exampleValues = new ArrayList<>();
        exampleValues.addAll(Arrays.asList(additional));
        exampleValues.addAll(EnumPropertyUtils.getEnumNames(enumClass));
        return exampleValues;
    }

    public static <T extends Enum<T>> List<String> getEnumNames(Class<T> enumClass) {
        final List<T> values = new ArrayList<>(EnumUtils.getEnumList(enumClass));
        return values.stream()
                   .map(Objects::toString)
                   .collect(Collectors.toList());
    }
}
