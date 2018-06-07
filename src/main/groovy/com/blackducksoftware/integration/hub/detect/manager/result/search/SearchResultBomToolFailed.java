package com.blackducksoftware.integration.hub.detect.manager.result.search;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.extraction.model.StrategyEvaluation;

public class SearchResultBomToolFailed extends SearchResult {
    private final BomToolException exception;

    public SearchResultBomToolFailed(final BomToolException exception) {
        this.exception = exception;
    }

    public BomToolException getException() {
        return exception;
    }

    @Override
    public List<StrategyEvaluation> getStrategyEvaluations() {
        return new ArrayList<>();
    }

    @Override
    public boolean getSuccess() {
        return false;
    }
}
