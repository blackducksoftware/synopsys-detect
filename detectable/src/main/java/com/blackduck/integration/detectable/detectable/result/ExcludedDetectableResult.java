package com.blackduck.integration.detectable.detectable.result;

public class ExcludedDetectableResult extends FailedDetectableResult {
    @Override
    public String toDescription() {
        return "Detector type was excluded.";
    }
}
