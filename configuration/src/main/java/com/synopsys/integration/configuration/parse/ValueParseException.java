package com.synopsys.integration.configuration.parse;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ValueParseException extends Exception {
    @NotNull
    private final String rawValue;
    @NotNull
    private final String typeName;
    @NotNull
    private final String additionalMessage;

    @Nullable
    private final Exception innerException;

    public ValueParseException(@NotNull String rawValue, @NotNull String typeName, @NotNull String additionalMessage) {
        this(rawValue, typeName, additionalMessage, null);
    }

    public ValueParseException(@NotNull String rawValue, @NotNull String typeName, @Nullable Exception innerException) {
        this(rawValue, typeName, "", innerException);
    }

    public ValueParseException(@NotNull String rawValue, @NotNull String typeName, @NotNull String additionalMessage, @Nullable Exception innerException) {
        super(String.format("Unable to parse raw value '%s' and coerce it into type '%s'. %s", rawValue, typeName, additionalMessage), innerException);
        this.rawValue = rawValue;
        this.typeName = typeName;
        this.additionalMessage = additionalMessage;
        this.innerException = innerException;
    }

    @NotNull
    public String getRawValue() {
        return rawValue;
    }

    @NotNull
    public String getTypeName() {
        return typeName;
    }

    @NotNull
    public String getAdditionalMessage() {
        return additionalMessage;
    }

    public Optional<Exception> getInnerException() {
        return Optional.ofNullable(innerException);
    }
}
