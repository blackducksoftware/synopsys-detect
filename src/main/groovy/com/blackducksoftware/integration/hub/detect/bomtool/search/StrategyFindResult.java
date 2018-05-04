package com.blackducksoftware.integration.hub.detect.bomtool.search;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation.StrategyEvaluation;

public class StrategyFindResult {
    public Strategy strategy;
    public FindType type;
    public StrategyEvaluation evaluation;
    public EvaluationContext context;

    public enum FindType {
        YIELDED,
        NEEDS_NOT_MET,
        APPLIES
    }

    public StrategyFindResult(final Strategy strategy, final FindType type, final StrategyEvaluation evaluation, final EvaluationContext context) {
        this.strategy = strategy;
        this.type = type;
        this.evaluation = evaluation;
        this.context = context;
    }
}
