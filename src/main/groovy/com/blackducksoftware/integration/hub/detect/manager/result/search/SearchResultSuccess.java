package com.blackducksoftware.integration.hub.detect.manager.result.search;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.model.StrategyEvaluation;

public class SearchResultSuccess extends SearchResult {
    List<StrategyEvaluation> strategyEvaluations;

    public SearchResultSuccess(final List<StrategyEvaluation> strategyEvaluations) {
        this.strategyEvaluations = strategyEvaluations;
    }

    @Override
    public List<StrategyEvaluation> getStrategyEvaluations() {
        return strategyEvaluations;
    }

    @Override
    public boolean getSuccess() {
        return true;
    }

}
