package com.synopsys.integration.detector.accuracy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.rule.DetectableDefinition;
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorExtract { //TODO: Rename?
    private final DetectableEvaluator detectableEvaluator;

    public DetectorExtract(DetectableEvaluator detectableEvaluator) {this.detectableEvaluator = detectableEvaluator;}

    public EntryPointEvaluation extract(EntryPoint entryPoint, DetectableEnvironment detectableEnvironment, Supplier<ExtractionEnvironment> extractionEnvironmentSupplier) {
        List<DetectableDefinition> toCascade = entryPoint.allDetectables();

        List<DetectableEvaluationResult> evaluated = new ArrayList<>();
        DetectableEvaluationResult extracted = null;

        for (DetectableDefinition detectable : toCascade) {
            DetectableEvaluationResult detectableEvaluationResult = detectableEvaluator.evaluate(detectable, detectableEnvironment, extractionEnvironmentSupplier);
            evaluated.add(detectableEvaluationResult);
            if (detectableEvaluationResult.wasExtractionSuccessful()) {
                extracted = detectableEvaluationResult;
                break;
            }
        }

        return new EntryPointEvaluation(evaluated, extracted);
    }

}
