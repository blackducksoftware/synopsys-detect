/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property;

//data class PropertyHelpInfo(val short: String, val long: String?)
public class PropertyHelpInfo {
    private final String shortText;
    private final String longText;

    public PropertyHelpInfo(final String shortText, final String longText) {
        this.shortText = shortText;
        this.longText = longText;
    }

    public String getShortText() {
        return shortText;
    }

    public String getLongText() {
        return longText;
    }
}
