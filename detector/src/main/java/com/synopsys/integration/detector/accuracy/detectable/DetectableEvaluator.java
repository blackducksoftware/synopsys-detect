package com.synopsys.integration.detector.accuracy.detectable;

import java.util.function.Supplier;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExceptionDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.rule.DetectableDefinition;

public class DetectableEvaluator {
    public DetectableEvaluationResult evaluate(
        DetectableDefinition detectableDefinition,
        DetectableEnvironment detectableEnvironment,
        Supplier<ExtractionEnvironment> extractionEnvironmentSupplier
    ) {
        Detectable detectable = detectableDefinition.getDetectableCreatable().createDetectable(detectableEnvironment);

        DetectableResult applicable = detectable.applicable();
        if (!applicable.getPassed()) {
            return DetectableEvaluationResult.notApplicable(detectableDefinition, applicable);
        }

        DetectableResult extractable;
        try {
            extractable = detectable.extractable();
            if (!extractable.getPassed()) {
                return DetectableEvaluationResult.notExtractable(detectableDefinition, applicable, extractable);
            }
        } catch (Exception e) {
            return DetectableEvaluationResult.notExtractable(detectableDefinition, applicable, new ExceptionDetectableResult(e));
        }

        ExtractionEnvironment extractionEnvironment = extractionEnvironmentSupplier.get();
        Extraction extraction;
        try {
            extraction = detectable.extract(extractionEnvironment);
        } catch (Exception e) {
            extraction = new Extraction.Builder().exception(e).build();
        }
        return DetectableEvaluationResult.extracted(detectableDefinition, applicable, extractable, extractionEnvironment, extraction);
    }
}
