package com.synopsys.integration.detector.accuracy.search;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.result.ExcludedDetectorResult;
import com.synopsys.integration.detector.result.ForcedNestedPassedDetectorResult;
import com.synopsys.integration.detector.result.MaxDepthExceededDetectorResult;
import com.synopsys.integration.detector.result.NotNestableBeneathDetectorResult;
import com.synopsys.integration.detector.result.NotSelfNestableDetectorResult;
import com.synopsys.integration.detector.result.NotSelfTypeNestableDetectorResult;
import com.synopsys.integration.detector.result.PassedDetectorResult;
import com.synopsys.integration.detector.result.YieldedDetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.SearchRule;

public class SearchEvaluator {
    private final SearchOptions searchOptions;

    public SearchEvaluator(SearchOptions searchOptions) {this.searchOptions = searchOptions;}

    public DetectorResult evaluateSearchable(DetectorType detectorType, SearchRule rule, SearchEnvironment environment) {
        if (!searchOptions.getDetectorFilter().test(detectorType)) {
            return new ExcludedDetectorResult();
        }

        int maxDepth = rule.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededDetectorResult(environment.getDepth(), maxDepth);
        }

        Set<DetectorType> yieldTo = environment.getAppliedSoFar().stream()
            .map(DetectorRule::getDetectorType)
            .filter(it -> rule.getYieldsTo().contains(it))
            .collect(Collectors.toSet());

        if (yieldTo.size() > 0) {
            return new YieldedDetectorResult(yieldTo.stream().map(Objects::toString)
                .collect(Collectors.toSet()));
        }

        boolean nestable = rule.isNestable();
        boolean selfNestable = rule.isSelfNestable();
        boolean selfTypeNestable = rule.isSelfTypeNestable();
        Set<DetectorType> notNestableBeneath = rule.getNotNestableBeneath();
        if (searchOptions.isForceNestedSearch()) {
            return new ForcedNestedPassedDetectorResult();
        } else if (nestable) {
            if (!selfNestable && environment.getAppliedToParent().stream().map(DetectorRule::getDetectorType).anyMatch(detectorType::equals)) {
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
            //} else if (environment.getAppliedToParent().stream().anyMatch(it -> !it.isNestInvisible())) { TODO: (Nest invisible?)
            //    return new NotNestableDetectorResult();
        }

        return new PassedDetectorResult();
    }
}
