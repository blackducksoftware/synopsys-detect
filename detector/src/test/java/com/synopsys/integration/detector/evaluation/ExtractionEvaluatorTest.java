package com.synopsys.integration.detector.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

//TODO: These tests should be re-written to use a concrete set of objects rather than mocks.
public class ExtractionEvaluatorTest {
    @Test
    public void testEvaluationSuccess() throws DetectableException, ExecutableFailedException {
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        ExtractionEvaluator evaluator = new ExtractionEvaluator(evaluationOptions);
        DetectorEvaluationTree detectorEvaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        Mockito.when(detectorEvaluationTree.getDirectory()).thenReturn(new File("."));
        DetectorEvaluatorListener detectorEvaluatorListener = Mockito.mock(DetectorEvaluatorListener.class);
        evaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        DetectorEvaluation detectorEvaluation = createEvaluationMocks(evaluationOptions, detectorEvaluationTree, false, false);

        DetectorAggregateEvaluationResult result = evaluator.evaluate(detectorEvaluationTree);

        assertEquals(detectorEvaluationTree, result.getEvaluationTree());

        Mockito.verify(detectorEvaluatorListener).extractionStarted(detectorEvaluation);
        Mockito.verify(detectorEvaluation).setExtraction(Mockito.any(Extraction.class));
        Mockito.verify(detectorEvaluatorListener).extractionEnded(detectorEvaluation);
    }

    @Test
    public void testEvaluationDiscoveryExtractionPerformed() throws DetectableException, ExecutableFailedException {
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        ExtractionEvaluator evaluator = new ExtractionEvaluator(evaluationOptions);
        DetectorEvaluationTree detectorEvaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        Mockito.when(detectorEvaluationTree.getDirectory()).thenReturn(new File("."));
        DetectorEvaluatorListener detectorEvaluatorListener = Mockito.mock(DetectorEvaluatorListener.class);
        evaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        DetectorEvaluation detectorEvaluation = createEvaluationMocks(evaluationOptions, detectorEvaluationTree, true, false);

        DetectorAggregateEvaluationResult result = evaluator.evaluate(detectorEvaluationTree);

        assertEquals(detectorEvaluationTree, result.getEvaluationTree());

        Mockito.verify(detectorEvaluatorListener).extractionStarted(detectorEvaluation);
        Mockito.verify(detectorEvaluation).setExtraction(Mockito.any(Extraction.class));
        Mockito.verify(detectorEvaluatorListener).extractionEnded(detectorEvaluation);
    }

    @Test
    public void testEvaluationException() throws DetectableException, ExecutableFailedException {
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        ExtractionEvaluator evaluator = new ExtractionEvaluator(evaluationOptions);
        DetectorEvaluationTree detectorEvaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        Mockito.when(detectorEvaluationTree.getDirectory()).thenReturn(new File("."));
        DetectorEvaluatorListener detectorEvaluatorListener = Mockito.mock(DetectorEvaluatorListener.class);
        evaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        DetectorEvaluation detectorEvaluation = createEvaluationMocks(evaluationOptions, detectorEvaluationTree, false, true);

        DetectorAggregateEvaluationResult result = evaluator.evaluate(detectorEvaluationTree);

        assertEquals(detectorEvaluationTree, result.getEvaluationTree());

        Mockito.verify(detectorEvaluatorListener).extractionStarted(detectorEvaluation);
        Mockito.verify(detectorEvaluation).setExtraction(Mockito.any(Extraction.class));
        Mockito.verify(detectorEvaluatorListener).extractionEnded(detectorEvaluation);
    }

    private DetectorEvaluation createEvaluationMocks(DetectorEvaluationOptions evaluationOptions, DetectorEvaluationTree detectorEvaluationTree, boolean extractionExists, boolean throwException)
        throws DetectableException, ExecutableFailedException {
        ExtractionEnvironment extractionEnvironment = Mockito.mock(ExtractionEnvironment.class);
        DetectorEvaluation detectorEvaluation = Mockito.mock(DetectorEvaluation.class);

        Detectable detectable = Mockito.mock(Detectable.class);
        DetectableResult detectableExtractableResult = Mockito.mock(DetectableResult.class);
        Mockito.when(detectableExtractableResult.getPassed()).thenReturn(true);
        Mockito.when(detectableExtractableResult.toDescription()).thenReturn("test detectable");
        Mockito.when(detectable.extractable()).thenReturn(detectableExtractableResult);
        Mockito.when(detectorEvaluation.getDetectable()).thenReturn(detectable);
        Mockito.when(detectorEvaluation.getExtractionEnvironment()).thenReturn(extractionEnvironment);
        Mockito.when(detectorEvaluation.isSearchable()).thenReturn(true);
        Mockito.when(detectorEvaluation.isApplicable()).thenReturn(true);
        Mockito.when(detectorEvaluation.isExtractable()).thenReturn(true);
        List<DetectorEvaluation> detectorEvaluations = Collections.singletonList(detectorEvaluation);
        Mockito.when(detectorEvaluationTree.getOrderedEvaluations()).thenReturn(detectorEvaluations);

        DetectorRuleSet detectorRuleSet = Mockito.mock(DetectorRuleSet.class);
        Mockito.when(detectorEvaluationTree.getDetectorRuleSet()).thenReturn(detectorRuleSet);

        DetectorRule detectorRule = Mockito.mock(DetectorRule.class);
        Mockito.when(detectorRule.getDescriptiveName()).thenReturn("test rule");
        Mockito.when(detectorEvaluation.getDetectorRule()).thenReturn(detectorRule);

        Mockito.when(detectorEvaluationTree.getDepthFromRoot()).thenReturn(0);
        Mockito.when(evaluationOptions.isForceNested()).thenReturn(true);
        Predicate<DetectorRule> rulePredicate = it -> true;
        Mockito.when(evaluationOptions.getDetectorFilter()).thenReturn(rulePredicate);

        Mockito.when(detectorRule.createDetectable(Mockito.any(DetectableEnvironment.class))).thenReturn(detectable);
        Mockito.when(detectable.applicable()).thenReturn(new PassedDetectableResult());
        if (throwException) {
            Mockito.when(detectable.extract(Mockito.eq(extractionEnvironment))).thenThrow(new RuntimeException("JUnit expected exception"));
        } else {
            Mockito.when(detectable.extract(Mockito.eq(extractionEnvironment))).thenReturn(new Extraction.Builder().success().build());
        }

        return detectorEvaluation;
    }
}
