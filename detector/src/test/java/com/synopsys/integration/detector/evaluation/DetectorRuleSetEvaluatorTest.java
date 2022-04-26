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
import com.synopsys.integration.detectable.detectables.xcode.XcodeWorkspaceDetectable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.result.NotNestableBeneathDetectorResult;
import com.synopsys.integration.detector.result.NotSelfTypeNestableDetectorResult;
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
        Set<DetectorRule> appliedToParent = new HashSet<>();
        Mockito.when(environment.getAppliedToParent()).thenReturn(appliedToParent);
        Mockito.when(detectorRule.isNestable()).thenReturn(true);
        Mockito.when(environment.isForceNestedSearch()).thenReturn(false);
        Mockito.when(detectorRule.isSelfTypeNestable()).thenReturn(false);
        Mockito.when(detectorRule.getDetectorType()).thenReturn(DetectorType.GRADLE);

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

    @Test
    public void nestableExceptByDetectorType() {
        DetectorRuleSetBuilder ruleSet = new DetectorRuleSetBuilder();

        DetectorRule workspaceRule = ruleSet.addDetector(DetectorType.XCODE, "Xcode Workspace", XcodeWorkspaceDetectable.class, (e) -> null)
            .defaults()
            .notSelfTypeNestable()
            .build();
        DetectorRule projectRule = ruleSet.addDetector(DetectorType.XCODE, "Xcode Project", XcodeProjectDetectable.class, (e) -> null)
            .defaults()
            .notSelfTypeNestable()
            .build();

        // XCODE applied at depth 0, we are now scanning a folder at depth 2.
        Set<DetectorRule> appliedToParent = Sets.newHashSet(workspaceRule);
        Set<DetectorRule> appliedSoFar = Sets.newHashSet();
        SearchEnvironment searchEnvironment = new SearchEnvironment(2, (d) -> true, false, false, appliedToParent, appliedSoFar);
        DetectorResult result = new DetectorRuleSetEvaluator().evaluateSearchable(ruleSet.build(), projectRule, searchEnvironment);

        Assertions.assertEquals(NotSelfTypeNestableDetectorResult.class, result.getClass());
    }
}
