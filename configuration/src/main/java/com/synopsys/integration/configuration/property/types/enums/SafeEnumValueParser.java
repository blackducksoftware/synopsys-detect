/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enums;

import java.util.Optional;

import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.NotNull;

public class SafeEnumValueParser<T extends Enum<T>> {
    private final Class<T> enumClass;

    public SafeEnumValueParser(@NotNull Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @NotNull
    public Optional<T> parse(@NotNull String value) {
        return Optional.ofNullable(EnumUtils.getEnumIgnoreCase(enumClass, value));
    }
}
