package com.blackducksoftware.integration.hub.detect.manager.result.search;

public class ExtractionId {
    private final String id;
    public ExtractionId(final String id) {
        this.id = id;
    }
    public String toUniqueString() {
        return id;
    }
}
