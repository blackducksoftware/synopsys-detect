package com.synopsys.integration.detector.accuracy;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorRuleEvaluation {
    @NotNull
    private final DetectableEnvironment environment;
    @NotNull
    private final DetectorRule rule;
    @NotNull
    private final EntryPoint selectedEntryPoint;
    @NotNull
    private final EntryPointEvaluation selectedEntryPointEvaluation;

    public DetectorRuleEvaluation(
        @NotNull DetectableEnvironment environment,
        @NotNull DetectorRule rule,
        @NotNull EntryPoint selectedEntryPoint,
        @NotNull EntryPointEvaluation selectedEntryPointEvaluation
    ) {
        this.environment = environment;
        this.rule = rule;
        this.selectedEntryPoint = selectedEntryPoint;
        this.selectedEntryPointEvaluation = selectedEntryPointEvaluation;
    }

    @NotNull
    public DetectorRule getRule() {
        return rule;
    }

    @NotNull
    public EntryPoint getSelectedEntryPoint() {
        return selectedEntryPoint;
    }

    @NotNull
    public EntryPointEvaluation getSelectedEntryPointEvaluation() {
        return selectedEntryPointEvaluation;
    }

    public Optional<DetectableEvaluationResult> getExtractedDetectableEvaluation() {
        return selectedEntryPointEvaluation.getExtractedEvaluation();
    }

    public Optional<Extraction> getExtraction() {
        return getExtractedDetectableEvaluation().map(DetectableEvaluationResult::getExtraction);
    }

    public boolean wasExtractionSuccessful() {
        return selectedEntryPointEvaluation.getExtractedEvaluation()
            .map(DetectableEvaluationResult::wasExtractionSuccessful)
            .orElse(false);
    }

    @NotNull
    public DetectableEnvironment getEnvironment() {
        return environment;
    }

}
