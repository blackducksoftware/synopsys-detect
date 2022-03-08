package com.synopsys.integration.detector.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

//TODO: These tests should be re-written to use a concrete set of objects rather than mocks.
public class DetectorEvaluatorTest {

    @Test
    public void testEvaluation() {
        DetectorEvaluatorListener listener = Mockito.mock(DetectorEvaluatorListener.class);
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        ExtractionEnvironment extractionEnvironment = Mockito.mock(ExtractionEnvironment.class);
        Function<DetectorEvaluation, ExtractionEnvironment> extractionEnvironmentProvider = (detectorEvaluation) -> extractionEnvironment;
        DetectorEvaluationTree rootEvaluation = Mockito.mock(DetectorEvaluationTree.class);

        DetectorEvaluator detectorEvaluator = new DetectorEvaluator(evaluationOptions, extractionEnvironmentProvider);
        detectorEvaluator.setDetectorEvaluatorListener(listener);
        DetectorAggregateEvaluationResult result = detectorEvaluator.evaluate(rootEvaluation);

        Optional<DetectorEvaluatorListener> actualListener = detectorEvaluator.getDetectorEvaluatorListener();
        assertTrue(actualListener.isPresent());
        assertEquals(listener, actualListener.get());
        assertEquals(rootEvaluation, result.getEvaluationTree());
    }

}
