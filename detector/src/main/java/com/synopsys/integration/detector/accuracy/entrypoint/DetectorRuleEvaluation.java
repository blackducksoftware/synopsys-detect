package com.synopsys.integration.detector.accuracy.entrypoint;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectorRuleEvaluation {
    @NotNull
    private final DetectableEnvironment environment;
    @NotNull
    private final DetectorRule rule;
    @NotNull
    private final List<EntryPointNotFoundResult> notFoundEntryPoints;
    @Nullable
    private final EntryPointFoundResult foundEntryPoint;

    public DetectorRuleEvaluation(
        @NotNull DetectorRule rule,
        @NotNull DetectableEnvironment environment,
        @NotNull List<EntryPointNotFoundResult> notFoundEntryPoints,
        @Nullable EntryPointFoundResult foundEntryPoint
    ) {
        this.environment = environment;
        this.rule = rule;
        this.notFoundEntryPoints = notFoundEntryPoints;
        this.foundEntryPoint = foundEntryPoint;
    }

    @NotNull
    public DetectorRule getRule() {
        return rule;
    }

    @NotNull
    public DetectableEnvironment getEnvironment() {
        return environment;
    }

    public List<EntryPointNotFoundResult> getNotFoundEntryPoints() {
        return notFoundEntryPoints;
    }

    public Optional<EntryPointFoundResult> getFoundEntryPoint() {
        return Optional.ofNullable(foundEntryPoint);
    }

    public boolean wasFound() {
        return getFoundEntryPoint().isPresent();
    }
}
