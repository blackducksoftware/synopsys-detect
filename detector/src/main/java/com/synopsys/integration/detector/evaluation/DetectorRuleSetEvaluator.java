package com.synopsys.integration.detector.evaluation;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.result.ExcludedDetectorResult;
import com.synopsys.integration.detector.result.ForcedNestedPassedDetectorResult;
import com.synopsys.integration.detector.result.MaxDepthExceededDetectorResult;
import com.synopsys.integration.detector.result.NotNestableBeneathDetectorResult;
import com.synopsys.integration.detector.result.NotNestableDetectorResult;
import com.synopsys.integration.detector.result.NotSelfNestableDetectorResult;
import com.synopsys.integration.detector.result.NotSelfTypeNestableDetectorResult;
import com.synopsys.integration.detector.result.PassedDetectorResult;
import com.synopsys.integration.detector.result.YieldedDetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorRuleSetEvaluator {
    public DetectorResult evaluateSearchable(DetectorRuleSet detectorRuleSet, DetectorRule detectorRule, SearchEnvironment environment) {
        if (!environment.getDetectorFilter().test(detectorRule)) {
            return new ExcludedDetectorResult();
        }

        int maxDepth = detectorRule.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededDetectorResult(environment.getDepth(), maxDepth);
        }

        Set<DetectorRule> yieldTo = environment.getAppliedSoFar().stream()
            .filter(it -> detectorRuleSet.getYieldsTo(detectorRule).contains(it))
            .collect(Collectors.toSet());

        if (yieldTo.size() > 0) {
            return new YieldedDetectorResult(yieldTo.stream().map(DetectorRule::getName).collect(Collectors.toSet()));
        }

        return evaluateNestable(detectorRule, environment);
    }

    private DetectorResult evaluateNestable(DetectorRule detectorRule, SearchEnvironment environment) {
        boolean nestable = detectorRule.isNestable();
        boolean selfNestable = detectorRule.isSelfNestable();
        boolean selfTypeNestable = detectorRule.isSelfTypeNestable();
        DetectorType detectorType = detectorRule.getDetectorType();
        Set<DetectorType> notNestableBeneath = detectorRule.getNotNestableBeneath();
        if (environment.isForceNestedSearch()) {
            return new ForcedNestedPassedDetectorResult();
        } else if (nestable) {
            if (!selfNestable && environment.getAppliedToParent().stream().anyMatch(detectorRule::equals)) {
                return new NotSelfNestableDetectorResult();
            }
            if (!selfTypeNestable && environment.getAppliedToParent().stream().map(DetectorRule::getDetectorType).anyMatch(detectorType::equals)) {
                return new NotSelfTypeNestableDetectorResult(detectorType);
            }
            if (notNestableBeneath.size() > 0) {
                Optional<DetectorType> notNestableBeneathType = environment.getAppliedToParent().stream()
                    .map(DetectorRule::getDetectorType)
                    .filter(notNestableBeneath::contains)
                    .findAny();
                if (notNestableBeneathType.isPresent()) {
                    return new NotNestableBeneathDetectorResult(notNestableBeneathType.get());
                }
            }
        } else if (environment.getAppliedToParent().stream().anyMatch(it -> !it.isNestInvisible())) {
            return new NotNestableDetectorResult();
        }
        return new PassedDetectorResult();
    }
}
