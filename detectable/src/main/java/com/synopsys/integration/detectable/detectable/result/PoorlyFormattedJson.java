package com.synopsys.integration.detectable.detectable.result;

public class PoorlyFormattedJson extends FailedDetectableResult {
    
    private final String jsonFilePath;

    public PoorlyFormattedJson(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    @Override
    public String toDescription() {
        return String.format("Attempted to parse %s but the file appears to not conform to JSON standards. Please ensure that the file is in JSON format and try again.",
                jsonFilePath);
    }
}
