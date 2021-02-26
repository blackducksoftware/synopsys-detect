/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enumsoft;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.types.enums.SafeEnumValueParser;

class SoftEnumValueParser<T extends Enum<T>> extends ValueParser<SoftEnumValue<T>> {
    private final SafeEnumValueParser<T> parser;

    public SoftEnumValueParser(@NotNull Class<T> enumClass) {
        this.parser = new SafeEnumValueParser<T>(enumClass);
    }

    @NotNull
    @Override
    public SoftEnumValue<T> parse(@NotNull final String value) throws ValueParseException {
        Optional<T> enumValue = parser.parse(value);
        return enumValue.map(SoftEnumValue::ofEnumValue)
                   .orElseGet(() -> SoftEnumValue.ofSoftValue(value));
    }
}