package com.synopsys.integration.configuration.property.types.bool;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;

class BooleanValueParser extends ValueParser<Boolean> {
    @NotNull
    @Override
    public Boolean parse(@NotNull String value) throws ValueParseException {
        String trimmed = value.toLowerCase().trim();
        if (StringUtils.isBlank(trimmed)) {
            return true;
        } else {
            Boolean aBoolean = BooleanUtils.toBooleanObject(trimmed);
            if (aBoolean == null) {
                throw new ValueParseException(value, "boolean", "Unknown boolean format. Supported values include true and false.");
            } else {
                return aBoolean;
            }
        }
    }
}