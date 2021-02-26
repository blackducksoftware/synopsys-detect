/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

    public ValueParseException(@NotNull final String rawValue, @NotNull final String typeName, @NotNull final String additionalMessage) {
        this(rawValue, typeName, additionalMessage, null);
    }

    public ValueParseException(@NotNull final String rawValue, @NotNull final String typeName, @Nullable final Exception innerException) {
        this(rawValue, typeName, "", innerException);
    }

    public ValueParseException(@NotNull final String rawValue, @NotNull final String typeName, @NotNull final String additionalMessage, @Nullable final Exception innerException) {
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
