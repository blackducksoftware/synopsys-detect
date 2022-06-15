package com.synopsys.integration.detector.rule.builder;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorRuleBuilder {
    private final DetectableLookup detectableLookup;
    private final DetectorType detectorType;
    private final List<EntryPointBuilder> entryPointBuilders = new ArrayList<>();
    private boolean allEntryPointsFallbackToNext = false;

    public DetectorRuleBuilder(DetectorType detectorType, DetectableLookup detectableLookup) {
        this.detectorType = detectorType;
        this.detectableLookup = detectableLookup;
    }

    public DetectorRule build() {
        return new DetectorRule(
            detectorType,
            buildEntryPoints()
        );
    }

    private List<EntryPoint> buildEntryPoints() {
        if (allEntryPointsFallbackToNext) {
            entryPointBuilders.forEach(EntryPointBuilder::fallbackToNextEntryPoint);
        }

        List<EntryPoint> entryPoints = new ArrayList<>();
        //Build these from back to front so the 'next' point can be passed along.
        EntryPoint next = null;
        for (int i = entryPointBuilders.size() - 1; i >= 0; i--) {
            EntryPointBuilder current = entryPointBuilders.get(i);
            next = current.build(detectableLookup, next);
            entryPoints.add(0, next);
        }

        return entryPoints;
    }

    public <T extends Detectable> EntryPointBuilder entryPoint(Class<T> detectableClass) {
        EntryPointBuilder builder = new EntryPointBuilder(detectableClass, detectorType);
        entryPointBuilders.add(builder);
        return builder;
    }

    public DetectorRuleBuilder allEntryPointsFallbackToNext() {
        allEntryPointsFallbackToNext = true;
        return this;
    }

    public DetectorRuleBuilder nestableExceptTo(DetectorType detectorType) {
        entryPointBuilders.forEach(builder -> builder.search().nestableExceptTo(detectorType));
        return this;
    }

    public DetectorRuleBuilder yieldsTo(DetectorType... detectorTypes) {
        entryPointBuilders.forEach(builder -> builder.search().yieldsTo(detectorTypes));
        return this;
    }
}
