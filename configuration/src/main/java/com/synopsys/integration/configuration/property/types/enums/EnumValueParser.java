/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enums;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

public class EnumValueParser<T extends Enum<T>> extends ValueParser<T> {
    private final Class<T> enumClass;
    private SafeEnumValueParser<T> parser;

    public EnumValueParser(@NotNull Class<T> enumClass) {
        this.enumClass = enumClass;
        this.parser = new SafeEnumValueParser<T>(enumClass);
    }

    @NotNull
    @Override
    public T parse(@NotNull String value) throws ValueParseException {
        Optional<T> enumValue = this.parser.parse(value);
        if (enumValue.isPresent()) {
            return enumValue.get();
        } else {
            throw new ValueParseException(value, enumClass.getSimpleName(), "Value '" + value + "' must be one of " + String.join(",", EnumPropertyUtils.getEnumNames(enumClass)));
        }
    }
}


