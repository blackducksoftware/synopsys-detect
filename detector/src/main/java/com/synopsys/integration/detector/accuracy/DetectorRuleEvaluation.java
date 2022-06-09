package com.synopsys.integration.detector.accuracy;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectorRuleEvaluation {
    @NotNull
    private final DetectableEnvironment environment;
    @NotNull
    private final DetectorRule rule;
    @NotNull
    private final List<DetectorSearchEntryPointResult> notFoundEntryPoints;
    @NotNull
    private final EntryPointEvaluation evaluatedEntryPoint;

    public DetectorRuleEvaluation(
        @NotNull DetectorRule rule,
        @NotNull DetectableEnvironment environment,
        @NotNull List<DetectorSearchEntryPointResult> notFoundEntryPoints,
        @NotNull EntryPointEvaluation evaluatedEntryPoint
    ) {
        this.environment = environment;
        this.rule = rule;
        this.evaluatedEntryPoint = evaluatedEntryPoint;
        this.notFoundEntryPoints = notFoundEntryPoints;
    }

    @NotNull
    public DetectorRule getRule() {
        return rule;
    }

    @NotNull
    public DetectableEnvironment getEnvironment() {
        return environment;
    }

    public List<DetectorSearchEntryPointResult> getNotFoundEntryPoints() {
        return notFoundEntryPoints;
    }

    public EntryPointEvaluation getEvaluatedEntryPoint() {
        return evaluatedEntryPoint;
    }
}
