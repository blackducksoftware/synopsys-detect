package com.blackducksoftware.integration.hub.detect.strategy.result;

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
