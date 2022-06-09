package com.synopsys.integration.detector.accuracy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
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
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorSearch {
    public DetectorSearchResult evaluate(SearchEnvironment searchEnvironment, DetectableEnvironment detectableEnvironment, DetectorRule detector) {
        DetectorType detectorType = detector.getDetectorType();
        DetectorResult searchableResult = evaluateSearchable(detector, searchEnvironment);
        if (!searchableResult.getPassed()) {
            return DetectorSearchResult.notSearchable(searchableResult);
        }

        List<DetectorSearchEntryPointResult> evaluated = new ArrayList<>();
        DetectorSearchEntryPointResult found = null;
        for (EntryPoint entryPoint : detector.getEntryPoints()) {
            Detectable primaryDetectable = entryPoint.getPrimary().getDetectableCreatable().createDetectable(detectableEnvironment);

            DetectableResult applicable = primaryDetectable.applicable();
            DetectorSearchEntryPointResult entryPointResult = new DetectorSearchEntryPointResult(entryPoint, applicable);
            if (applicable.getPassed()) {
                found = entryPointResult;
                break;
            } else {
                evaluated.add(entryPointResult);
            }
        }

        if (found != null) {
            return DetectorSearchResult.found(found, evaluated);
        } else {
            return DetectorSearchResult.notFound(evaluated);
        }
    }

    private DetectorResult evaluateSearchable(DetectorRule detectorRule, SearchEnvironment environment) {
        if (!environment.getDetectorFilter().test(detectorRule)) {
            return new ExcludedDetectorResult();
        }

        int maxDepth = detectorRule.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededDetectorResult(environment.getDepth(), maxDepth);
        }

        Set<DetectorType> yieldTo = environment.getAppliedSoFar().stream()
            .map(DetectorRule::getDetectorType)
            .filter(it -> detectorRule.getYieldsTo().contains(it))
            .collect(Collectors.toSet());

        if (yieldTo.size() > 0) {
            return new YieldedDetectorResult(yieldTo.stream().map(Objects::toString)
                .collect(Collectors.toSet()));
        }

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
