package com.synopsys.integration.detector.rule.builder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.SearchRule;

public class SearchRuleBuilder {
    private final DetectorType owningDetectorType;

    private int maxDepth;

    private boolean nestable;
    private final Set<DetectorType> notNestableBeneath = new HashSet<>();
    private final Set<Class<?>> notNestableBeneathDetectables = new HashSet<>();

    private final Set<DetectorType> yieldsTo = new HashSet<>();

    public SearchRuleBuilder(DetectorType owningDetectorType) {
        this.owningDetectorType = owningDetectorType;
    }

    public SearchRuleBuilder defaults() {
        return noMaxDepth()
            .nestable()
            .notNestableBeneath(owningDetectorType);
    }

    public SearchRuleBuilder defaultLock() {
        return noMaxDepth()
            .nestable();
    }

    public SearchRuleBuilder noMaxDepth() {
        return maxDepth(Integer.MAX_VALUE);
    }

    public SearchRuleBuilder maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public SearchRuleBuilder isNestable(boolean nestable) {
        this.nestable = nestable;
        return this;
    }

    public SearchRuleBuilder nestable() {
        return isNestable(true);
    }

    public SearchRuleBuilder notNestableBeneath(DetectorType... detectorType) {
        notNestableBeneath.addAll(Arrays.asList(detectorType));
        return this;
    }

    public SearchRuleBuilder notNestableBeneath(Class<?>... detectable) {
        notNestableBeneathDetectables.addAll(Arrays.asList(detectable));
        return this;
    }

    public SearchRuleBuilder nestableExceptTo(DetectorType... detectorType) {
        return nestable().notNestableBeneath(detectorType);
    }

    public SearchRule build(@NotNull DetectableLookup lookup) {
        return new SearchRule(
            maxDepth,
            nestable,
            notNestableBeneath,
            notNestableBeneathDetectables.stream()
                .map(lookup::forClass)
                .collect(Collectors.toSet()),
            yieldsTo
        );
    }

    public SearchRuleBuilder yieldsTo(DetectorType... detectorTypes) {
        yieldsTo.addAll(Arrays.asList(detectorTypes));
        return this;
    }

    public void notSelfNestable() {
        notNestableBeneath(owningDetectorType);
    }
}
