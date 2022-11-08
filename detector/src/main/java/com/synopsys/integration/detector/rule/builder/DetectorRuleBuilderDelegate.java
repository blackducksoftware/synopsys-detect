package com.synopsys.integration.detector.rule.builder;

@FunctionalInterface
public interface DetectorRuleBuilderDelegate {
    void build(DetectorRuleBuilder builder);
}
