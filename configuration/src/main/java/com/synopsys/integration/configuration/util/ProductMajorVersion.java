/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.util;

import org.jetbrains.annotations.NotNull;

public class ProductMajorVersion {
    private final Integer intValue;

    public ProductMajorVersion(final Integer intValue) {
        this.intValue = intValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    @NotNull
    public String getDisplayValue() {
        return String.format("%s.0.0", getIntValue());
    }
}