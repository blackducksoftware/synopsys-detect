/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
