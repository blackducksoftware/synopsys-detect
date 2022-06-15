package com.synopsys.integration.detector.rule;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class EntryPoint {
    private final DetectableDefinition primary;
    private final List<DetectableDefinition> fallbacks;
    private final SearchRule searchRule;

    public EntryPoint(DetectableDefinition primary, List<DetectableDefinition> fallbacks, SearchRule searchRule) {
        this.primary = primary;
        this.fallbacks = fallbacks;
        this.searchRule = searchRule;
    }

    public DetectableDefinition getPrimary() {
        return primary;
    }

    public List<DetectableDefinition> getFallbacks() {
        return fallbacks;
    }

    @NotNull
    public List<DetectableDefinition> allDetectables() {
        List<DetectableDefinition> combined = new ArrayList<>();
        combined.add(primary);
        combined.addAll(fallbacks);
        return combined;
    }

    public SearchRule getSearchRule() {
        return searchRule;
    }
}
