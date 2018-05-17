package com.blackducksoftware.integration.hub.detect.strategy.result;

public class BomToolExcludedStrategyResult extends FailedStrategyResult {
    @Override
    public String toDescription() {
        return "Bom tool type was excluded.";
    }
}
