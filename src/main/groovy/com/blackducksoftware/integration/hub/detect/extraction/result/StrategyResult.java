package com.blackducksoftware.integration.hub.detect.extraction.result;

public abstract class StrategyResult {
    public abstract boolean getPassed();
    public abstract String toDescription();
}
