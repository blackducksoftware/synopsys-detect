package com.synopsys.integration.detector.accuracy;

import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectorRuleNotFoundResult {
    private final DetectorRule rule;
    private final DetectorSearchResult searchResult;

    public DetectorRuleNotFoundResult(DetectorRule rule, DetectorSearchResult searchResult) {
        this.rule = rule;
        this.searchResult = searchResult;
    }

    public DetectorRule getRule() {
        return rule;
    }

    public DetectorSearchResult getSearchResult() {
        return searchResult;
    }
}
