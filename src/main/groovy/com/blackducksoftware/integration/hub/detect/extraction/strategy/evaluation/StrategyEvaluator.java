package com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContextAction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator.RequirementEvaluatorManager;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;

public class StrategyEvaluator {

    @Autowired
    public RequirementEvaluatorManager requirementEvaluatorManager;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void fulfillsRequirements(final StrategyEvaluation strategyEvaluation, final Strategy strategy, final EvaluationContext evaluationContext) {
        final Set<Requirement> requirements = strategy.getRequirements();
        for (final Requirement requirement : requirements) {
            final RequirementEvaluation requirementEvaluation = requirementEvaluatorManager.evaluate(requirement, evaluationContext);
            strategyEvaluation.addRequirementEvaluation(requirement, requirementEvaluation);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void fulfillsDemands(final StrategyEvaluation strategyEvaluation, final Strategy strategy, final EvaluationContext evaluationContext) {
        final Set<Requirement> requirements = strategy.getRequirements();
        for (final Requirement requirement : requirements) {
            final RequirementEvaluation requirementEvaluation = requirementEvaluatorManager.evaluate(requirement, evaluationContext);
            strategyEvaluation.addRequirementEvaluation(requirement, requirementEvaluation);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute(final StrategyEvaluation strategyEvaluation, final Strategy strategy, final EvaluationContext evaluationContext) {
        final ExtractionContext context = strategy.newContext();

        final Set<Requirement> requirements = strategy.getRequirements();
        for (final Requirement requirement : requirements) {
            final RequirementEvaluation requirementEvaluation = strategyEvaluation.getRequirementEvaluation(requirement);
            final ExtractionContextAction action = strategy.getAction(requirement);
            action.perform(context, requirementEvaluation.value);
        }

        final Extractor extractor = strategy.newExtractor();
        extractor.extract(context);

    }

}
