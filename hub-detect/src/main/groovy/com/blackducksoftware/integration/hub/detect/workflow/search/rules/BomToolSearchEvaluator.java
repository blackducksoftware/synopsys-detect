package com.blackducksoftware.integration.hub.detect.workflow.search.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolExcludedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ForcedNestedPassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.MaxDepthExceededBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.NotNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.NotSelfNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.YieldedBomToolResult;

public class BomToolSearchEvaluator {

    public List<BomToolEvaluation> evaluate(BomToolSearchRuleSet rules, EventSystem eventSystem) {
        final List<BomToolEvaluation> evaluations = new ArrayList<>();
        final List<Detector> appliedSoFar = new ArrayList<>();
        for (final BomToolSearchRule searchRule : rules.getOrderedBomToolRules()) {
            final Detector detector = searchRule.getDetector();
            final BomToolEvaluation evaluation = new BomToolEvaluation(detector, rules.getEnvironment());
            evaluations.add(evaluation);
            evaluation.setSearchable(searchable(searchRule, appliedSoFar, rules.getEnvironment()));
            if (evaluation.isSearchable()) {
                eventSystem.publishEvent(Event.ApplicableStarted, detector);
                evaluation.setApplicable(detector.applicable());
                eventSystem.publishEvent(Event.ApplicableEnded, detector);
                if (evaluation.isApplicable()) {
                    appliedSoFar.add(detector);
                }
            }
        }
        return evaluations;
    }

    public BomToolResult searchable(final BomToolSearchRule searchRules, final List<Detector> appliedSoFar, DetectorEnvironment environment) {
        Detector detector = searchRules.getDetector();
        final DetectorType detectorType = detector.getDetectorType();
        if (!environment.getBomToolFilter().shouldInclude(detectorType.toString())) {
            return new BomToolExcludedBomToolResult();
        }

        final int maxDepth = searchRules.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededBomToolResult(environment.getDepth(), maxDepth);
        }

        final Set<Detector> yieldTo = appliedSoFar.stream()
                                          .filter(it -> searchRules.getYieldsTo().contains(it))
                                          .collect(Collectors.toSet());

        if (yieldTo.size() > 0) {
            return new YieldedBomToolResult(yieldTo);
        }

        final boolean nestable = searchRules.isNestable();
        if (environment.getForceNestedSearch()) {
            return new ForcedNestedPassedBomToolResult();
        } else if (nestable) {
            if (environment.getAppliedToParent().stream().anyMatch(applied -> applied.isSame(detector))) {
                return new NotSelfNestableBomToolResult();
            }
        } else if (!nestable && environment.getAppliedToParent().size() > 0) {
            return new NotNestableBomToolResult();
        }

        return new PassedBomToolResult();
    }
}
