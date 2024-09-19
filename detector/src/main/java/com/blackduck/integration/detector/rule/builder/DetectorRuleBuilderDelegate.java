package com.blackduck.integration.detector.rule.builder;

@FunctionalInterface
public interface DetectorRuleBuilderDelegate {
    void build(DetectorRuleBuilder builder);
}
