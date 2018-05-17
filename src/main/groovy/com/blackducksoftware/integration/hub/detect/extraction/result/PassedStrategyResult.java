package com.blackducksoftware.integration.hub.detect.extraction.result;

public class PassedStrategyResult extends StrategyResult {
    @Override
    public boolean getPassed() {
        return true;
    }

    @Override
    public String toDescription() {
        return "Passed.";
    }
}
