package com.blackduck.integration.detect.interactive;

import com.blackduck.integration.configuration.property.Property;

public class InteractiveOption {
    private Property detectProperty;
    private String interactiveValue;

    public Property getDetectProperty() {
        return detectProperty;
    }

    public void setDetectProperty(Property detectProperty) {
        this.detectProperty = detectProperty;
    }

    public String getInteractiveValue() {
        return interactiveValue;
    }

    public void setInteractiveValue(String interactiveValue) {
        this.interactiveValue = interactiveValue;
    }

}
