package com.synopsys.integration.detectable.detectable.explanation;

public class PropertyProvided extends Explanation {
    private final String property;

    public PropertyProvided(String property) {
        this.property = property;
    }

    @Override
    public String describeSelf() {
        return "Property provided: " + property;
    }
}
