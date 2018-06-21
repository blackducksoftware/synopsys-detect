package com.blackducksoftware.integration.hub.detect.bomtool;

public class ExtractionId {
    private final String id;
    public ExtractionId(final String id) {
        this.id = id;
    }
    public String toUniqueString() {
        return id;
    }
}
