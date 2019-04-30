package com.synopsys.integration.detector.evaluation;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectorEvaluatorTest {

    @Test
    public void test() {
        final DetectorEvaluationOptions evaluationOptions = Mockito.mock( DetectorEvaluationOptions.class);
        final DetectorEvaluator evaluator = new DetectorEvaluator(evaluationOptions);
        final DetectorEvaluationTree detectorEvaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        Mockito.when(detectorEvaluationTree.getDirectory()).thenReturn(new File("."));
        final Set<DetectorRule> appliedInParent = new HashSet<>();

        final DetectorEvaluation detectorEvaluation = Mockito.mock(DetectorEvaluation.class);
        Mockito.when(detectorEvaluation.isApplicable()).thenReturn(true);
        List<DetectorEvaluation> detectorEvaluations = Arrays.asList(detectorEvaluation);
        Mockito.when(detectorEvaluationTree.getOrderedEvaluations()).thenReturn(detectorEvaluations);

        final DetectorEvaluatorListener detectorEvaluatorListener = Mockito.mock(DetectorEvaluatorListener.class);
        evaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);

        DetectorRule detectorRule = Mockito.mock(DetectorRule.class);
        Mockito.when(detectorRule.getDescriptiveName()).thenReturn("test rule");
        Mockito.when(detectorEvaluation.getDetectorRule()).thenReturn(detectorRule);

        Mockito.when(detectorEvaluationTree.getDepthFromRoot()).thenReturn(0);
        Mockito.when(evaluationOptions.isForceNested()).thenReturn(true);
        final Predicate<DetectorRule> x = it -> true;
        Mockito.when(evaluationOptions.getDetectorFilter()).thenReturn(x);
        Mockito.when(detectorEvaluation.isSearchable()).thenReturn(true);

        final Detectable detectable = Mockito.mock(Detectable.class);
        Mockito.when(detectorRule.createDetectable(Mockito.any(DetectableEnvironment.class))).thenReturn(detectable);
        Mockito.when(detectable.applicable()).thenReturn(new PassedDetectableResult());

        evaluator.searchAndApplicableEvaluation(detectorEvaluationTree, appliedInParent);

        Mockito.verify(detectorEvaluatorListener).applicableStarted(detectorEvaluation);
        Mockito.verify(detectorRule).createDetectable(Mockito.any(DetectableEnvironment.class));
        Mockito.verify(detectorEvaluation).setDetectableEnvironment(Mockito.any(DetectableEnvironment.class));
        Mockito.verify(detectorEvaluation).setDetectable(detectable);
        Mockito.verify(detectorEvaluatorListener).applicableEnded(detectorEvaluation);
    }
}
