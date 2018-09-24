package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

public enum ResourceType {
    RegistrationBaseUrl("RegistrationBaseUrl");

    private final String type;

    ResourceType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
