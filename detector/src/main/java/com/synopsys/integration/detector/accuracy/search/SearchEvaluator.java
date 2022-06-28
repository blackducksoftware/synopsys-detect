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
import com.synopsys.integration.detector.result.NotNestableBeneathDetectableDetectorResult;
import com.synopsys.integration.detector.result.NotNestableBeneathDetectorResult;
import com.synopsys.integration.detector.result.PassedDetectorResult;
import com.synopsys.integration.detector.result.YieldedDetectorResult;
import com.synopsys.integration.detector.rule.DetectableDefinition;
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
            .filter(it -> rule.getYieldsTo().contains(it))
            .collect(Collectors.toSet());

        if (yieldTo.size() > 0) {
            return new YieldedDetectorResult(yieldTo.stream().map(Objects::toString)
                .collect(Collectors.toSet()));
        }

        boolean nestable = rule.isNestable();
        Set<DetectorType> notNestableBeneathDetectors = rule.getNotNestableBeneath();
        Set<String> notNestableBeneathDetectableNames = rule.getNotNestableBeneathDetectables().stream()
            .map(DetectableDefinition::getName)
            .collect(Collectors.toSet());

        if (searchOptions.isForceNestedSearch()) {
            return new ForcedNestedPassedDetectorResult();
        } else if (nestable) {
            if (notNestableBeneathDetectors.size() > 0) {
                Optional<DetectorType> notNestableBeneathType = environment.getAppliedToParent().stream()
                    .filter(notNestableBeneathDetectors::contains)
                    .findAny();
                if (notNestableBeneathType.isPresent()) {
                    return new NotNestableBeneathDetectorResult(notNestableBeneathType.get());
                }
            }
            if (notNestableBeneathDetectableNames.size() > 0) {
                Optional<String> notNestableBeneathName = environment.getExtractedInParent().stream()
                    .map(DetectableDefinition::getName)
                    .filter(notNestableBeneathDetectableNames::contains)
                    .findAny();
                if (notNestableBeneathName.isPresent()) {
                    return new NotNestableBeneathDetectableDetectorResult(notNestableBeneathName.get());
                }
            }
        }

        return new PassedDetectorResult();
    }
}
