package com.blackduck.integration.configuration.property.types.enumsoft;

import java.util.Optional;

import com.blackduck.integration.configuration.parse.ValueParseException;
import com.blackduck.integration.configuration.parse.ValueParser;
import com.blackduck.integration.configuration.property.types.enums.SafeEnumValueParser;
import org.jetbrains.annotations.NotNull;

class SoftEnumValueParser<T extends Enum<T>> extends ValueParser<SoftEnumValue<T>> {
    private final SafeEnumValueParser<T> parser;

    public SoftEnumValueParser(@NotNull Class<T> enumClass) {
        this.parser = new SafeEnumValueParser<>(enumClass);
    }

    @NotNull
    @Override
    public SoftEnumValue<T> parse(@NotNull String value) throws ValueParseException {
        Optional<T> enumValue = parser.parse(value);
        return enumValue.map(SoftEnumValue::ofEnumValue)
            .orElseGet(() -> SoftEnumValue.ofSoftValue(value));
    }
}