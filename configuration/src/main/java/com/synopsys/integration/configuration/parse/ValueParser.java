/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.parse;

import org.jetbrains.annotations.NotNull;

public abstract class ValueParser<T> {
    @NotNull
    public abstract T parse(@NotNull final String value) throws ValueParseException;
}
