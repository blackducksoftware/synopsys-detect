package com.synopsys.integration.detectable.detectable.result;

public class SectionNotFoundDetectableResult extends FailedDetectableResult {
    private final String fileName;
    private final String missingSection;

    public SectionNotFoundDetectableResult(String fileName, String missingSection) {
        this.fileName = fileName;
        this.missingSection = missingSection;
    }

    @Override
    public String toDescription() {
        return String.format("%s was not found in %s.", missingSection, fileName);
    }

}
