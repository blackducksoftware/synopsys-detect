package com.synopsys.integration.detector.evaluation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.jsonldjava.shaded.com.google.common.collect.Sets;
import com.synopsys.integration.detectable.detectables.swift.cli.SwiftCliDetectable;
import com.synopsys.integration.detectable.detectables.xcode.XcodeProjectDetectable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.result.NotNestableBeneathDetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.detector.rule.DetectorRuleSetBuilder;

public class DetectorRuleSetEvaluatorTest {

    @Test
    public void test() {

        DetectorRuleSet detectorRuleSet = Mockito.mock(DetectorRuleSet.class);
        DetectorRule detectorRule = Mockito.mock(DetectorRule.class);
        SearchEnvironment environment = Mockito.mock(SearchEnvironment.class);

        Predicate<DetectorRule> rulePredicate = it -> true;
        Mockito.when(environment.getDetectorFilter()).thenReturn(rulePredicate);
        Mockito.when(detectorRule.getMaxDepth()).thenReturn(1);
        Mockito.when(environment.getDepth()).thenReturn(0);
        Set<DetectorRule> appliedSoFar = new HashSet<>();
        Mockito.when(environment.getAppliedSoFar()).thenReturn(appliedSoFar);
        Mockito.when(detectorRule.isNestable()).thenReturn(true);
        Mockito.when(environment.isForceNestedSearch()).thenReturn(false);

        DetectorRuleSetEvaluator evaluator = new DetectorRuleSetEvaluator();
        DetectorResult result = evaluator.evaluateSearchable(detectorRuleSet, detectorRule, environment);

        assertTrue(result.getPassed());
    }

    @Test
    public void nestableExcept() {
        DetectorRuleSetBuilder ruleSet = new DetectorRuleSetBuilder();

        DetectorRule xcode = ruleSet.addDetector(DetectorType.XCODE, "Xcode", XcodeProjectDetectable.class, (e) -> null).defaults().build();
        DetectorRule swift = ruleSet.addDetector(DetectorType.SWIFT, "Swift", SwiftCliDetectable.class, (e) -> null).defaults().nestableExceptTo(DetectorType.XCODE).build();

        //XCODE applied at depth 0, we are now scanning a folder at depth 2.
        Set<DetectorRule> appliedToParent = Sets.newHashSet(xcode);
        Set<DetectorRule> appliedSoFar = Sets.newHashSet();
        SearchEnvironment searchEnvironment = new SearchEnvironment(2, (d) -> true, false, false, appliedToParent, appliedSoFar);
        DetectorResult result = new DetectorRuleSetEvaluator().evaluateSearchable(ruleSet.build(), swift, searchEnvironment);

        Assertions.assertEquals(NotNestableBeneathDetectorResult.class, result.getClass());
    }
}
