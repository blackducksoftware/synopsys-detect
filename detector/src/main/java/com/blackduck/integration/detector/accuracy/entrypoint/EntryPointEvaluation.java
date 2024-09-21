package com.blackduck.integration.detector.accuracy.entrypoint;

import java.util.List;

import com.blackduck.integration.detector.accuracy.detectable.DetectableEvaluationResult;
import com.blackduck.integration.detector.rule.EntryPoint;

public class EntryPointEvaluation {
    private final EntryPoint entryPoint;
    private final List<DetectableEvaluationResult> evaluatedDetectables;

    public EntryPointEvaluation(
        EntryPoint entryPoint, List<DetectableEvaluationResult> evaluatedDetectables
    ) {
        this.entryPoint = entryPoint;
        this.evaluatedDetectables = evaluatedDetectables;
    }

    public List<DetectableEvaluationResult> getEvaluatedDetectables() {
        return evaluatedDetectables;
    }

    public EntryPoint getEntryPoint() {
        return entryPoint;
    }
}
