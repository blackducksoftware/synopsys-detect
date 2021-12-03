package com.synopsys.integration.detect.configuration.validation;

import java.util.Map;

public class DeprecationResult {
    private final Map<String, String> additionalNotes;

    public DeprecationResult(Map<String, String> additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public Map<String, String> getAdditionalNotes() {
        return additionalNotes;
    }
}