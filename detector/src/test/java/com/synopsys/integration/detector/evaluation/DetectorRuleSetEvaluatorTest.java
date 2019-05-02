package com.synopsys.integration.detector.evaluation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorRuleSetEvaluatorTest {

    @Test
    public void test() {

        final DetectorRuleSet detectorRuleSet = Mockito.mock(DetectorRuleSet.class);
        final DetectorRule detectorRule = Mockito.mock(DetectorRule.class);
        final SearchEnvironment environment = Mockito.mock(SearchEnvironment.class);

        final Predicate<DetectorRule> rulePredicate = it -> true;
        Mockito.when(environment.getDetectorFilter()).thenReturn(rulePredicate);
        Mockito.when(detectorRule.getMaxDepth()).thenReturn(1);
        Mockito.when(environment.getDepth()).thenReturn(0);
        final Set<DetectorRule> appliedSoFar = new HashSet<>();
        Mockito.when(environment.getAppliedSoFar()).thenReturn(appliedSoFar);
        Mockito.when(detectorRule.isNestable()).thenReturn(true);
        Mockito.when(environment.isForceNestedSearch()).thenReturn(false);

        final DetectorRuleSetEvaluator evaluator = new DetectorRuleSetEvaluator();
        final DetectorResult result = evaluator.evaluateSearchable(detectorRuleSet, detectorRule, environment);

        assertTrue(result.getPassed());
    }
}
