package com.synopsys.integration.detector.rule;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class EntryPoint {
    private final DetectableDefinition primary;
    private final List<DetectableDefinition> fallbacks;

    public EntryPoint(DetectableDefinition primary, List<DetectableDefinition> fallbacks) {
        this.primary = primary;
        this.fallbacks = fallbacks;
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
}
