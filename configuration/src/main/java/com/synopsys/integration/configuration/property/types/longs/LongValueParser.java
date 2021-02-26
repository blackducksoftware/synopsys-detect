/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.longs;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;

public class LongValueParser extends ValueParser<Long> {
    @NotNull
    @Override
    public Long parse(@NotNull final String value) throws ValueParseException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ValueParseException(value, "long", e);
        }
    }
}
