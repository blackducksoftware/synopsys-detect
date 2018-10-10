package com.blackducksoftware.integration.hub.detect.workflow.search.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.event.Event;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
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
        final List<BomTool> appliedSoFar = new ArrayList<>();
        for (final BomToolSearchRule searchRule : rules.getOrderedBomToolRules()) {
            final BomTool bomTool = searchRule.getBomTool();
            final BomToolEvaluation evaluation = new BomToolEvaluation(bomTool, rules.getEnvironment());
            evaluations.add(evaluation);
            evaluation.setSearchable(searchable(searchRule, appliedSoFar, rules.getEnvironment()));
            if (evaluation.isSearchable()) {
                eventSystem.publishEvent(Event.ApplicableStarted, bomTool);
                evaluation.setApplicable(bomTool.applicable());
                eventSystem.publishEvent(Event.ApplicableEnded, bomTool);
                if (evaluation.isApplicable()) {
                    appliedSoFar.add(bomTool);
                }
            }
        }
        return evaluations;
    }

    public BomToolResult searchable(final BomToolSearchRule searchRules, final List<BomTool> appliedSoFar, BomToolEnvironment environment) {
        final BomToolGroupType bomToolGroupType = searchRules.getBomTool().getBomToolGroupType();
        if (!environment.getBomToolFilter().shouldInclude(bomToolGroupType.toString())) {
            return new BomToolExcludedBomToolResult();
        }

        final int maxDepth = searchRules.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededBomToolResult(environment.getDepth(), maxDepth);
        }

        final Set<BomTool> yielded = appliedSoFar.stream()
                                         .filter(it -> searchRules.getYieldsTo().contains(it.getBomToolType()))
                                         .collect(Collectors.toSet());

        if (yielded.size() > 0) {
            return new YieldedBomToolResult(yielded);
        }

        final BomToolType bomToolType = searchRules.getBomTool().getBomToolType();
        final boolean nestable = searchRules.isNestable();
        if (environment.getForceNestedSearch()) {
            return new ForcedNestedPassedBomToolResult();
        } else if (nestable) {
            if (environment.getAppliedToParent().contains(bomToolType)) {
                return new NotSelfNestableBomToolResult();
            }
        } else if (!nestable && environment.getAppliedToParent().size() > 0) {
            return new NotNestableBomToolResult();
        }

        return new PassedBomToolResult();
    }
}
