package com.blackduck.integration.configuration.property.types.enums;

import java.util.Optional;

import com.blackduck.integration.configuration.parse.ValueParseException;
import com.blackduck.integration.configuration.parse.ValueParser;
import com.blackduck.integration.configuration.util.EnumPropertyUtils;
import org.jetbrains.annotations.NotNull;

public class EnumValueParser<T extends Enum<T>> extends ValueParser<T> {
    private final Class<T> enumClass;
    private final SafeEnumValueParser<T> parser;

    public EnumValueParser(@NotNull Class<T> enumClass) {
        this.enumClass = enumClass;
        this.parser = new SafeEnumValueParser<>(enumClass);
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


