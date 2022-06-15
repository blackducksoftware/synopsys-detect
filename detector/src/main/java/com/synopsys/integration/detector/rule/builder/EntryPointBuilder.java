package com.synopsys.integration.detector.rule.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectableDefinition;
import com.synopsys.integration.detector.rule.EntryPoint;

public class EntryPointBuilder {
    private final Class<?> primary;
    private final List<Class<?>> fallbacks;

    private boolean fallbackToNextEntryPoint = false;
    private final SearchRuleBuilder searchRuleBuilder;

    public EntryPointBuilder(Class<?> primary, DetectorType detectorType) {
        this.primary = primary;
        this.fallbacks = new ArrayList<>();
        searchRuleBuilder = new SearchRuleBuilder(detectorType);
    }

    public void fallbackToNextEntryPoint() {
        fallbackToNextEntryPoint = true;
    }

    public <T extends Detectable> EntryPointBuilder fallback(Class<T> detectableClass) {
        fallbacks.add(detectableClass);
        return this;
    }

    public SearchRuleBuilder search() {
        return searchRuleBuilder;
    }

    public EntryPoint build(@NotNull DetectableLookup lookup, @Nullable EntryPoint nextEntryPoint) {
        DetectableDefinition primaryDefinition = lookup.forClass(primary);
        List<DetectableDefinition> fallbackDefinitions = fallbacks.stream()
            .map(lookup::forClass)
            .collect(Collectors.toList());

        if (fallbackToNextEntryPoint && nextEntryPoint != null) {
            fallbackDefinitions.addAll(nextEntryPoint.allDetectables());
        }
        return new EntryPoint(primaryDefinition, fallbackDefinitions, searchRuleBuilder.build(lookup));
    }
}
