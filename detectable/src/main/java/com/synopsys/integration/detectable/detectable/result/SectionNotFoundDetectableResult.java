package com.synopsys.integration.detectable.detectable.result;

public class SectionNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "%s was not found in %s.";

    public SectionNotFoundDetectableResult(String fileName, String missingSection) {
        super(String.format(FORMAT, missingSection, fileName));
    }
}
