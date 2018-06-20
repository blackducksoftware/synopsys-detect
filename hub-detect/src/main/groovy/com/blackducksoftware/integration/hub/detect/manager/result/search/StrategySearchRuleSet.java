package com.blackducksoftware.integration.hub.detect.manager.result.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.extraction.model.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.result.BomToolExcludedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.ForcedNestedPassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.MaxDepthExceededStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.NotNestableStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.NotSelfNestableStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.YieldedStrategyResult;

public class StrategySearchRuleSet {

    private final List<StrategySearchRules> orderedStrategyRules;
    private final StrategyEnvironment environment;

    public StrategySearchRuleSet(final List<StrategySearchRules> orderedStrategyRules, final StrategyEnvironment environment) {
        this.orderedStrategyRules = orderedStrategyRules;
        this.environment = environment;
    }

    public List<StrategyEvaluation> evaluate() {
        final List<StrategyEvaluation> evaluations = new ArrayList<>();
        final List<Strategy> appliedSoFar = new ArrayList<>();
        for (final StrategySearchRules searchRules : orderedStrategyRules) {
            final StrategyEvaluation evaluation = new StrategyEvaluation(searchRules.getStrategy(), environment);
            evaluations.add(evaluation);
            evaluation.searchable = searchable(searchRules, appliedSoFar);
            if (evaluation.isSearchable()) {
                evaluation.applicable = evaluation.strategy.applicable();
                if (evaluation.isApplicable()) {
                    appliedSoFar.add(searchRules.getStrategy());
                }
            }
        }
        return evaluations;
    }

    public StrategyResult searchable(final StrategySearchRules searchRules, final List<Strategy> appliedSoFar) {
        final BomToolType bomToolType = searchRules.getStrategy().getBomToolType();
        if (!environment.getBomToolFilter().shouldInclude(bomToolType.toString())) {
            return new BomToolExcludedStrategyResult();
        }

        final int maxDepth = searchRules.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededStrategyResult(environment.getDepth(), maxDepth);
        }

        final Set<Strategy> yielded = appliedSoFar.stream()
                .filter(it -> searchRules.getYieldsTo().contains(it.getStrategyType()))
                .collect(Collectors.toSet());

        if (yielded.size() > 0) {
            return new YieldedStrategyResult(yielded);
        }

        final StrategyType strategyType = searchRules.getStrategy().getStrategyType();
        final boolean nestable = searchRules.isNestable();
        if (environment.getForceNestedSearch()) {
            return new ForcedNestedPassedStrategyResult();
        } else if (nestable) {
            if (environment.getAppliedToParent().contains(strategyType)) {
                return new NotSelfNestableStrategyResult();
            }
        } else if (!nestable && environment.getAppliedToParent().size() > 0) {
            return new NotNestableStrategyResult();
        }

        return new PassedStrategyResult();
    }
}
