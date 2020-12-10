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
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.rule.DetectorRule;

public class ApplicableEvaluatorTest {
    @Test
    public void testEvaluationSuccess() {
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        ApplicableEvaluator evaluator = new ApplicableEvaluator(evaluationOptions);
        DetectorEvaluatorListener detectorEvaluatorListener = Mockito.mock(DetectorEvaluatorListener.class);
        evaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        DetectorEvaluationTree detectorEvaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        Mockito.when(detectorEvaluationTree.getDirectory()).thenReturn(new File("."));
        DetectorEvaluation detectorEvaluation = createEvaluationMocks(evaluationOptions, detectorEvaluationTree, false, true);

        DetectorAggregateEvaluationResult result = evaluator.evaluate(detectorEvaluationTree);

        assertEquals(detectorEvaluationTree, result.getEvaluationTree());

        Mockito.verify(detectorEvaluatorListener).applicableStarted(detectorEvaluation);
        Mockito.verify(detectorEvaluation).setDetectableEnvironment(Mockito.any(DetectableEnvironment.class));
        Mockito.verify(detectorEvaluation).setDetectable(Mockito.any(Detectable.class));
        Mockito.verify(detectorEvaluatorListener).applicableEnded(detectorEvaluation);
    }

    @Test
    public void testEvaluationNotSearchable() {
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        ApplicableEvaluator evaluator = new ApplicableEvaluator(evaluationOptions);
        DetectorEvaluatorListener detectorEvaluatorListener = Mockito.mock(DetectorEvaluatorListener.class);
        evaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        DetectorEvaluationTree detectorEvaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        Mockito.when(detectorEvaluationTree.getDirectory()).thenReturn(new File("."));
        DetectorEvaluation detectorEvaluation = createEvaluationMocks(evaluationOptions, detectorEvaluationTree, false, false);

        DetectorAggregateEvaluationResult result = evaluator.evaluate(detectorEvaluationTree);

        assertEquals(detectorEvaluationTree, result.getEvaluationTree());

        Mockito.verify(detectorEvaluatorListener).applicableStarted(detectorEvaluation);
        Mockito.verify(detectorEvaluatorListener).applicableEnded(detectorEvaluation);
    }

    @Test
    public void testEvaluationNotApplicable() {
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        ApplicableEvaluator evaluator = new ApplicableEvaluator(evaluationOptions);
        DetectorEvaluatorListener detectorEvaluatorListener = Mockito.mock(DetectorEvaluatorListener.class);
        evaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        DetectorEvaluationTree detectorEvaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        Mockito.when(detectorEvaluationTree.getDirectory()).thenReturn(new File("."));
        DetectorEvaluation detectorEvaluation = createEvaluationMocks(evaluationOptions, detectorEvaluationTree, true, true);

        DetectorAggregateEvaluationResult result = evaluator.evaluate(detectorEvaluationTree);

        assertEquals(detectorEvaluationTree, result.getEvaluationTree());

        Mockito.verify(detectorEvaluatorListener).applicableStarted(detectorEvaluation);
        Mockito.verify(detectorEvaluation).setDetectableEnvironment(Mockito.any(DetectableEnvironment.class));
        Mockito.verify(detectorEvaluation).setDetectable(Mockito.any(Detectable.class));
        Mockito.verify(detectorEvaluatorListener).applicableEnded(detectorEvaluation);
    }

    private DetectorEvaluation createEvaluationMocks(DetectorEvaluationOptions evaluationOptions, DetectorEvaluationTree detectorEvaluationTree, boolean alreadyApplicable, boolean searchable) {
        DetectorEvaluation detectorEvaluation = Mockito.mock(DetectorEvaluation.class);

        List<DetectorEvaluation> detectorEvaluations = Collections.singletonList(detectorEvaluation);
        Mockito.when(detectorEvaluationTree.getOrderedEvaluations()).thenReturn(detectorEvaluations);

        DetectorRule detectorRule = Mockito.mock(DetectorRule.class);
        Mockito.when(detectorRule.getDescriptiveName()).thenReturn("test rule");
        Mockito.when(detectorEvaluation.getDetectorRule()).thenReturn(detectorRule);
        Mockito.when(detectorEvaluation.isApplicable()).thenReturn(alreadyApplicable);
        Mockito.when(detectorEvaluation.isSearchable()).thenReturn(searchable);

        Mockito.when(detectorEvaluationTree.getDepthFromRoot()).thenReturn(0);
        Mockito.when(evaluationOptions.isForceNested()).thenReturn(true);
        Predicate<DetectorRule> rulePredicate = it -> true;
        Mockito.when(evaluationOptions.getDetectorFilter()).thenReturn(rulePredicate);
        Mockito.when(detectorEvaluation.isSearchable()).thenReturn(true);

        Detectable detectable = Mockito.mock(Detectable.class);
        Mockito.when(detectorRule.createDetectable(Mockito.any(DetectableEnvironment.class))).thenReturn(detectable);
        Mockito.when(detectable.applicable()).thenReturn(new FailedDetectableResult());
        return detectorEvaluation;
    }
}
