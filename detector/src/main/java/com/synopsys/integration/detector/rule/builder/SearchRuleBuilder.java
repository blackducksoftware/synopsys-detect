package com.synopsys.integration.detector.rule.builder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.SearchRule;

public class SearchRuleBuilder {
    private int maxDepth;
    private boolean nestable;
    private boolean selfNestable = false;

    private boolean selfTypeNestable = false;
    private boolean nestInvisible = false;
    private final Set<DetectorType> notNestableBeneath = new HashSet<>();
    private final Set<Class<?>> notNestableBeneathDetectables = new HashSet<>();
    private final Set<DetectorType> yieldsTo = new HashSet<>();

    public SearchRuleBuilder() {
    }

    public SearchRuleBuilder defaults() {
        return noMaxDepth().nestable().notSelfNestable().notSelfTypeNestable().visibleToNesting();
    }

    public SearchRuleBuilder defaultLock() {
        return noMaxDepth().nestable().selfNestable().selfTypeNestable().visibleToNesting();
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

    public SearchRuleBuilder isSelfNestable(boolean selfNestable) {
        this.selfNestable = selfNestable;
        return this;
    }

    public SearchRuleBuilder isNestInvisible(boolean nestable) {
        this.nestInvisible = nestable;
        return this;
    }

    public SearchRuleBuilder invisibleToNesting() {
        return isNestInvisible(true);
    }

    public SearchRuleBuilder visibleToNesting() {
        return isNestInvisible(false);
    }

    public SearchRuleBuilder nestable() {
        return isNestable(true);
    }

    public SearchRuleBuilder notNestable() {
        return isNestable(false);
    }

    public SearchRuleBuilder selfNestable() {
        return isSelfNestable(true);
    }

    public SearchRuleBuilder notSelfNestable() {
        return isSelfNestable(false);
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

    // Not self nestable by DetectorType rather than the Rule itself
    public SearchRuleBuilder notSelfTypeNestable() {
        selfTypeNestable = false;
        return this;
    }

    public SearchRuleBuilder selfTypeNestable() {
        selfTypeNestable = true;
        return this;
    }

    public SearchRule build() {
        return new SearchRule(
            maxDepth,
            nestable,
            selfNestable,
            selfTypeNestable,
            nestInvisible,
            notNestableBeneath,
            notNestableBeneathDetectables,
            yieldsTo
        );
    }

    public SearchRuleBuilder yieldsTo(DetectorType... detectorTypes) {
        yieldsTo.addAll(Arrays.asList(detectorTypes));
        return this;
    }
}
