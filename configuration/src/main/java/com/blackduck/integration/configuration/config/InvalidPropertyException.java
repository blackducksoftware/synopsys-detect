package com.blackduck.integration.configuration.config;

import com.blackduck.integration.configuration.parse.ValueParseException;

public class InvalidPropertyException extends RuntimeException {
    public InvalidPropertyException(String propertyKey, String propertySourceName, ValueParseException innerException) {
        super(String.format(
            "The key '%s' in property source '%s' contained a value that could not be reasonably converted to the properties type. The exception was: %s",
            propertyKey,
            propertySourceName,
            innerException.getLocalizedMessage() != null ? innerException.getLocalizedMessage() : "Unknown"
        ), innerException);
    }
}
