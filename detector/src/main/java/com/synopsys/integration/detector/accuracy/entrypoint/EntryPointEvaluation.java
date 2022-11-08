package com.synopsys.integration.detector.accuracy.entrypoint;

import java.util.List;

import com.synopsys.integration.detector.accuracy.detectable.DetectableEvaluationResult;
import com.synopsys.integration.detector.rule.EntryPoint;

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
