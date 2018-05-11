package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.util.Set;

import com.blackducksoftware.integration.hub.detect.extraction.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;

public class StrategyFindResult {
    public Strategy strategy;
    public FindType type;
    public Reason reason;
    public StrategyEvaluation evaluation;
    public EvaluationContext context;

    public int depth = 0;
    public Set<Strategy> nested = null;
    //best way to handle new reasons? should reasons be classes? ReasonYielded, ReasonNeedsNotMet, ReasonNotNestable?
    //with .toString() returning their print?

    public enum FindType {
        APPLIES,
        DOES_NOT_APPLY
    }

    public enum Reason {
        NONE,
        YIELDED,
        NEEDS_NOT_MET,
        NOT_NESTABLE,
        MAX_DEPTH_EXCEEDED
    }

    public StrategyFindResult(final Strategy strategy, final FindType type, final Reason reason, final StrategyEvaluation evaluation, final EvaluationContext context) {
        this.strategy = strategy;
        this.type = type;
        this.evaluation = evaluation;
        this.context = context;
        this.reason = reason;
    }
}
