package com.synopsys.integration.detector.rule.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorRuleBuilder {
    private final DetectableLookup detectableLookup;
    private int maxDepth;
    private boolean nestable;
    private boolean selfNestable = false;

    private boolean selfTypeNestable = false;
    private boolean nestInvisible = false;
    private final Set<DetectorType> notNestableBeneath = new HashSet<>();

    private final DetectorType detectorType;
    private List<EntryPointBuilder> entryPointBuilders;
    private Set<DetectorType> yieldsTo;
    private boolean allEntryPointsFallbackToNext = false;

    public DetectorRuleBuilder(DetectorType detectorType, DetectableLookup detectableLookup) {
        this.detectorType = detectorType;
        this.detectableLookup = detectableLookup;
    }

    public DetectorRuleBuilder defaults() {
        return allEntryPointsFallbackToNext().noMaxDepth().nestable().notSelfNestable().notSelfTypeNestable().visibleToNesting();
    }

    public DetectorRuleBuilder defaultLock() {
        return allEntryPointsFallbackToNext().noMaxDepth().nestable().selfNestable().selfTypeNestable().visibleToNesting();
    }

    public DetectorRuleBuilder noMaxDepth() {
        return maxDepth(Integer.MAX_VALUE);
    }

    public DetectorRuleBuilder maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public DetectorRuleBuilder isNestable(boolean nestable) {
        this.nestable = nestable;
        return this;
    }

    public DetectorRuleBuilder isSelfNestable(boolean selfNestable) {
        this.selfNestable = selfNestable;
        return this;
    }

    public DetectorRuleBuilder isNestInvisible(boolean nestable) {
        this.nestInvisible = nestable;
        return this;
    }

    public DetectorRuleBuilder invisibleToNesting() {
        return isNestInvisible(true);
    }

    public DetectorRuleBuilder visibleToNesting() {
        return isNestInvisible(false);
    }

    public DetectorRuleBuilder nestable() {
        return isNestable(true);
    }

    public DetectorRuleBuilder notNestable() {
        return isNestable(false);
    }

    public DetectorRuleBuilder selfNestable() {
        return isSelfNestable(true);
    }

    public DetectorRuleBuilder notSelfNestable() {
        return isSelfNestable(false);
    }

    public DetectorRuleBuilder notNestableBeneath(DetectorType... detectorType) {
        notNestableBeneath.addAll(Arrays.asList(detectorType));
        return this;
    }

    public DetectorRuleBuilder nestableExceptTo(DetectorType... detectorType) {
        return nestable().notNestableBeneath(detectorType);
    }

    // Not self nestable by DetectorType rather than the Rule itself
    public DetectorRuleBuilder notSelfTypeNestable() {
        selfTypeNestable = false;
        return this;
    }

    public DetectorRuleBuilder selfTypeNestable() {
        selfTypeNestable = true;
        return this;
    }

    public DetectorRule build() {
        return new DetectorRule(
            maxDepth,
            nestable,
            selfNestable,
            selfTypeNestable,
            detectorType,
            nestInvisible,
            notNestableBeneath,
            buildEntryPoints(),
            yieldsTo
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
            entryPoints.add(next);
        }

        return entryPoints;
    }

    public <T extends Detectable> EntryPointBuilder entryPoint(Class<T> detectableClass) {
        EntryPointBuilder builder = new EntryPointBuilder(detectableClass);
        entryPointBuilders.add(builder);
        return builder;
    }

    public DetectorRuleBuilder yieldsTo(DetectorType... detectorTypes) {
        yieldsTo.addAll(Arrays.asList(detectorTypes));
        return this;
    }

    public DetectorRuleBuilder allEntryPointsFallbackToNext() {
        allEntryPointsFallbackToNext = true;
        return this;
    }

}
