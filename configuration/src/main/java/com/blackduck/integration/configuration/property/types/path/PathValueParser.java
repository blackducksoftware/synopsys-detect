package com.blackduck.integration.configuration.property.types.path;

import com.blackduck.integration.configuration.parse.ValueParseException;
import com.blackduck.integration.configuration.parse.ValueParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class PathValueParser extends ValueParser<PathValue> {
    @NotNull
    @Override
    public PathValue parse(@NotNull String value) throws ValueParseException {
        String trimmedValue = value.trim();
        if (StringUtils.isNotBlank(trimmedValue)) {
            return new PathValue(trimmedValue);
        } else {
            throw new ValueParseException(trimmedValue, "Path", "A path must have at least one non-whitespace character!");
        }
    }
}
