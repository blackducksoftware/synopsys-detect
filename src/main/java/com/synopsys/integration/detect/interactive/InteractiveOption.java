/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.interactive;

import com.synopsys.integration.configuration.property.Property;

public class InteractiveOption {
    private Property detectProperty;
    private String interactiveValue;

    public Property getDetectProperty() {
        return detectProperty;
    }

    public void setDetectProperty(final Property detectProperty) {
        this.detectProperty = detectProperty;
    }

    public String getInteractiveValue() {
        return interactiveValue;
    }

    public void setInteractiveValue(final String interactiveValue) {
        this.interactiveValue = interactiveValue;
    }

}
