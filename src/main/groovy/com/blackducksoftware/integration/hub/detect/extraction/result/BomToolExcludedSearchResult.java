package com.blackducksoftware.integration.hub.detect.extraction.result;

public class BomToolExcludedSearchResult extends FailedStrategyResult {
    @Override
    public String toDescription() {
        return "Bom tool type was excluded.";
    }
}
