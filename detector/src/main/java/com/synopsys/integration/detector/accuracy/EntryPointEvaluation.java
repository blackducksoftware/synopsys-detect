package com.synopsys.integration.detector.accuracy;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class EntryPointEvaluation {
    private final List<DetectableEvaluationResult> evaluatedDetectables;
    @Nullable
    private final DetectableEvaluationResult extractedEvaluation;

    public EntryPointEvaluation(
        List<DetectableEvaluationResult> evaluatedDetectables,
        @Nullable DetectableEvaluationResult extractedEvaluation
    ) {
        this.evaluatedDetectables = evaluatedDetectables;
        this.extractedEvaluation = extractedEvaluation;
    }

    public Optional<DetectableEvaluationResult> getExtractedEvaluation() {
        return Optional.ofNullable(extractedEvaluation);
    }

    public List<DetectableEvaluationResult> getEvaluatedDetectables() {
        return evaluatedDetectables;
    }
}
