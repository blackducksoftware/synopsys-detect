package com.blackducksoftware.integration.hub.detect.extraction.result;

public class FailedStrategyResult extends StrategyResult {

    @Override
    public boolean getPassed() {
        return false;
    }

    @Override
    public String toDescription() {
        return "Passed.";
    }
}
