package com.blackducksoftware.integration.hub.detect.bomtool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolExcludedBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.ForcedNestedPassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.MaxDepthExceededBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.NotNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.NotSelfNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.YieldedBomToolResult;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.model.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;

public class BomToolSearchRuleSet {

    private final List<BomToolSearchRule> orderedBomToolRules;
    private final BomToolEnvironment environment;

    public BomToolSearchRuleSet(final List<BomToolSearchRule> orderedBomToolRules, final BomToolEnvironment environment) {
        this.orderedBomToolRules = orderedBomToolRules;
        this.environment = environment;
    }

    public List<BomToolEvaluation> evaluate() {
        final List<BomToolEvaluation> evaluations = new ArrayList<>();
        final List<BomTool> appliedSoFar = new ArrayList<>();
        for (final BomToolSearchRule searchRules : orderedBomToolRules) {
            final BomToolEvaluation evaluation = new BomToolEvaluation(searchRules.getBomTool(), environment);
            evaluations.add(evaluation);
            evaluation.searchable = searchable(searchRules, appliedSoFar);
            if (evaluation.isSearchable()) {
                evaluation.applicable = evaluation.bomTool.applicable();
                if (evaluation.isApplicable()) {
                    appliedSoFar.add(searchRules.getBomTool());
                }
            }
        }
        return evaluations;
    }

    public BomToolResult searchable(final BomToolSearchRule searchRules, final List<BomTool> appliedSoFar) {
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
