package com.blackducksoftware.integration.hub.detect.extraction;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;

public class StrategyEvaluation {
    public Strategy strategy;
    public ExtractionContext context;
    public Extraction extraction;
    public Applicable applicable;
    public Extractable extractable;
    public List<Strategy> yieldedTo = new ArrayList<>();

    public void addYieldedStrategy(final Strategy strategy) {
        yieldedTo.add(strategy);
    }

    public List<Strategy> getYieldedTo() {
        return yieldedTo;
    }


}
