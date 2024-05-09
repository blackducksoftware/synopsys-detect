package com.synopsys.integration.detectable.detectable.result;

public class PoorlyFormattedJson extends FailedDetectableResult {
    private static final String FORMAT = "Attempted to parse %s but the file appears to not conform to JSON standards. Please ensure that the file is in JSON format and try again.";

    public PoorlyFormattedJson(String jsonFilePath) {
        super(String.format(FORMAT, jsonFilePath));
    }
}
