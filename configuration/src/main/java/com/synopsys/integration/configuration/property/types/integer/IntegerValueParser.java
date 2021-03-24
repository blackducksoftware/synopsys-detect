/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.integer;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;

class IntegerValueParser extends ValueParser<Integer> {
    @NotNull
    @Override
    public Integer parse(@NotNull String value) throws ValueParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ValueParseException(value, "integer", e);
        }
    }
}
