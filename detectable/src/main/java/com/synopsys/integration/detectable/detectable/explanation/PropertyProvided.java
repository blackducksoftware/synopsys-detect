/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.explanation;

public class PropertyProvided extends Explanation {
    private final String property;

    public PropertyProvided(String property) {
        this.property = property;
    }

    @Override
    public String describeSelf() {
        return "Property provded: " + property;
    }
}
