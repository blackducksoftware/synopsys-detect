package com.synopsys.integration.detector.evaluation;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import org.jetbrains.annotations.NotNull;
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

        DetectorType detectorType = DetectorType.GRADLE;
        DetectorEvaluationTree evaluationTree = generateDetectorEvaluationTreeMock(detectorType);

        DetectorAggregateEvaluationResult result = new DetectorAggregateEvaluationResult(evaluationTree);

        assertEquals(1, result.getApplicableDetectorTypes().size());
        assertTrue(result.getApplicableDetectorTypes().contains(detectorType));
    }

    @Test
    public void testMultiLevel() {

        DetectorType topLevelDetectorType = DetectorType.GRADLE;
        DetectorType secondLevelDetectorType = DetectorType.MAVEN;

        DetectorEvaluationTree topLevelEvaluationTree = generateDetectorEvaluationTreeMock(topLevelDetectorType);
        DetectorEvaluationTree secondLevelEvaluationTree = generateDetectorEvaluationTreeMock(secondLevelDetectorType);
        Set<DetectorEvaluationTree> secondLevelEvaluationTrees = new HashSet<>();
        secondLevelEvaluationTrees.add(secondLevelEvaluationTree);
        Mockito.when(topLevelEvaluationTree.getChildren()).thenReturn(secondLevelEvaluationTrees);

        DetectorAggregateEvaluationResult result = new DetectorAggregateEvaluationResult(topLevelEvaluationTree);

        assertEquals(1, result.getApplicableDetectorTypes().size());
        assertEquals(2, result.getApplicableDetectorTypesRecursively().size());
        assertTrue(result.getApplicableDetectorTypesRecursively().contains(topLevelDetectorType));
        assertTrue(result.getApplicableDetectorTypesRecursively().contains(secondLevelDetectorType));
    }

    @NotNull
    private DetectorEvaluationTree generateDetectorEvaluationTreeMock(DetectorType detectorType) {
        DetectorEvaluationTree evaluationTree = Mockito.mock(DetectorEvaluationTree.class);
        List<DetectorEvaluation> evaluations = new ArrayList<>();
        DetectorEvaluation topLevelEvaluation = Mockito.mock(DetectorEvaluation.class);
        Mockito.when(topLevelEvaluation.isApplicable()).thenReturn(true);
        DetectorRule rule = Mockito.mock(DetectorRule.class);
        Mockito.when(rule.getDetectorType()).thenReturn(detectorType);
        Mockito.when(topLevelEvaluation.getDetectorRule()).thenReturn(rule);
        evaluations.add(topLevelEvaluation);
        Mockito.when(evaluationTree.getOrderedEvaluations()).thenReturn(evaluations);
        return evaluationTree;
    }
}
