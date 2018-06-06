package com.blackducksoftware.integration.hub.detect.manager.result.search;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.model.StrategyEvaluation;

public abstract class SearchResult {
    public SearchResult() {
    }

    public abstract List<StrategyEvaluation> getStrategyEvaluations();

    public abstract boolean getSuccess();

}
