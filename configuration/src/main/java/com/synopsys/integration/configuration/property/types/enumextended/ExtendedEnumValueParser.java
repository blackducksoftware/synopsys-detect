/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enumextended;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.types.enums.SafeEnumValueParser;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

class ExtendedEnumValueParser<E extends Enum<E>, B extends Enum<B>> extends ValueParser<ExtendedEnumValue<E, B>> {
    private final Class<E> enumClassE;
    private final Class<B> enumClassB;
    private final SafeEnumValueParser<E> extendedParser;
    private final SafeEnumValueParser<B> baseParser;

    public ExtendedEnumValueParser(@NotNull final Class<E> enumClassE, @NotNull final Class<B> enumClassB) {
        this.enumClassE = enumClassE;
        this.enumClassB = enumClassB;
        this.extendedParser = new SafeEnumValueParser<>(enumClassE);
        this.baseParser = new SafeEnumValueParser<>(enumClassB);
    }

    @Override
    @NotNull
    public ExtendedEnumValue<E, B> parse(@NotNull final String value) throws ValueParseException {
        final Optional<E> eValue = extendedParser.parse(value);
        if (eValue.isPresent()) {
            return ExtendedEnumValue.ofExtendedValue(eValue.get());
        }
        final Optional<B> bValue = baseParser.parse(value);
        if (bValue.isPresent()) {
            return ExtendedEnumValue.ofBaseValue(bValue.get());
        }
        final List<String> combinedOptions = new ArrayList<>();
        combinedOptions.addAll(EnumPropertyUtils.getEnumNames(enumClassE));
        combinedOptions.addAll(EnumPropertyUtils.getEnumNames(enumClassB));
        final String optionText = String.join(",", combinedOptions);
        throw new ValueParseException(value, enumClassE.getSimpleName() + " or " + enumClassB.getSimpleName(), "Value was must be one of " + optionText);
    }
}

