package com.synopsys.integration.detector.evaluation;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DetectorAggregateEvaluationResultTest {

    @Test
    public void testSingleLevel() {
        DetectorEvaluationTree evaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        List<DetectorEvaluation> evaluations = new ArrayList<>();

        DetectorEvaluation topLevelEvaluation = Mockito.mock(DetectorEvaluation.class);
        Mockito.when(topLevelEvaluation.isApplicable()).thenReturn(true);
        DetectorRule rule = Mockito.mock(DetectorRule.class);
        DetectorType detectorType = DetectorType.GRADLE;
        Mockito.when(rule.getDetectorType()).thenReturn(detectorType);
        Mockito.when(topLevelEvaluation.getDetectorRule()).thenReturn(rule);
        evaluations.add(topLevelEvaluation);
        Mockito.when(evaluationTree.getOrderedEvaluations()).thenReturn(evaluations);

        DetectorAggregateEvaluationResult result = new DetectorAggregateEvaluationResult(evaluationTree);

        assertEquals(1, result.getApplicableDetectorTypes().size());
        assertTrue(result.getApplicableDetectorTypes().contains(detectorType));
    }

    @Test
    public void testMultiLevel() {
        DetectorEvaluationTree topLevelEvaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        DetectorEvaluationTree secondLevelEvaluationTree = Mockito.mock(DetectorEvaluationTree.class);

        List<DetectorEvaluation> topLevelEvaluations = new ArrayList<>();
        List<DetectorEvaluation> secondLevelEvaluations = new ArrayList<>();

        DetectorEvaluation topLevelEvaluation = Mockito.mock(DetectorEvaluation.class);
        Mockito.when(topLevelEvaluation.isApplicable()).thenReturn(true);
        DetectorRule topLevelRule = Mockito.mock(DetectorRule.class);
        DetectorType topLevelDetectorType = DetectorType.GRADLE;
        Mockito.when(topLevelRule.getDetectorType()).thenReturn(topLevelDetectorType);
        Mockito.when(topLevelEvaluation.getDetectorRule()).thenReturn(topLevelRule);
        topLevelEvaluations.add(topLevelEvaluation);

        DetectorEvaluation secondLevelEvaluation = Mockito.mock(DetectorEvaluation.class);
        Mockito.when(secondLevelEvaluation.isApplicable()).thenReturn(true);
        DetectorRule secondLevelRule = Mockito.mock(DetectorRule.class);
        DetectorType secondLevelDetectorType = DetectorType.MAVEN;
        Mockito.when(secondLevelRule.getDetectorType()).thenReturn(secondLevelDetectorType);
        Mockito.when(secondLevelEvaluation.getDetectorRule()).thenReturn(secondLevelRule);
        secondLevelEvaluations.add(secondLevelEvaluation);

        Set<DetectorEvaluationTree> secondLevelEvaluationTrees = new HashSet<>();
        secondLevelEvaluationTrees.add(secondLevelEvaluationTree);

        Mockito.when(topLevelEvaluationTree.getOrderedEvaluations()).thenReturn(topLevelEvaluations);
        Mockito.when(topLevelEvaluationTree.getChildren()).thenReturn(secondLevelEvaluationTrees);
        Mockito.when(secondLevelEvaluationTree.getOrderedEvaluations()).thenReturn(secondLevelEvaluations);

        DetectorAggregateEvaluationResult result = new DetectorAggregateEvaluationResult(topLevelEvaluationTree);

        assertEquals(1, result.getApplicableDetectorTypes().size());
        assertEquals(2, result.getApplicableDetectorTypesRecursively().size());
        assertTrue(result.getApplicableDetectorTypesRecursively().contains(topLevelDetectorType));
        assertTrue(result.getApplicableDetectorTypesRecursively().contains(secondLevelDetectorType));
    }
}
