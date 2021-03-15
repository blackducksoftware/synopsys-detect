/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.string;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;

class StringValueParser extends ValueParser<String> {

    @NotNull
    @Override
    public String parse(@NotNull final String value) {
        return value;
    }
}
