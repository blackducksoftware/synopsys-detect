package com.blackducksoftware.integration.hub.detect.strategy.result;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.strategy.Strategy;


public class YieldedStrategyResult extends FailedStrategyResult {
    private final Set<Strategy> yieldedTo;

    public YieldedStrategyResult(final Strategy yielded) {
        yieldedTo = new HashSet<>();
        yieldedTo.add(yielded);
    }

    public YieldedStrategyResult(final Set<Strategy> yieldedTo) {
        this.yieldedTo = yieldedTo;
    }

    @Override
    public String toDescription() {
        final String yielded = yieldedTo.stream().map(it -> it.toString()).collect(Collectors.joining(", "));
        return "Yielded to strategies: " + yielded;
    }
}
