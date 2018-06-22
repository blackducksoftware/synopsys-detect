package com.blackducksoftware.integration.hub.detect.configuration;

public enum DetectPropertyType {
    BOOLEAN("Boolean"), STRING("String"), STRING_ARRAY("String[]"), INTEGER("Integer"), LONG("Long");

    private final String displayName;

    DetectPropertyType(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
