package com.blackducksoftware.integration.hub.detect.strategy.result;

public abstract class StrategyResult {
    public abstract boolean getPassed();
    public abstract String toDescription();
}
