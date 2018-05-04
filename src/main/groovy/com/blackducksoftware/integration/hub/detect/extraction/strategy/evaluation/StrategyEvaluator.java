package com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContextAction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluatorManager;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;

@Component
public class StrategyEvaluator {

    @Autowired
    public RequirementEvaluatorManager requirementEvaluatorManager;

    @Autowired
    public List<Extractor> autowiredExtractors;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void fulfillsRequirements(final StrategyEvaluation strategyEvaluation, final Strategy strategy, final EvaluationContext evaluationContext) {
        final Set<Requirement> requirements = strategy.getNeeds();
        for (final Requirement requirement : requirements) {
            final RequirementEvaluation requirementEvaluation = requirementEvaluatorManager.evaluate(requirement, evaluationContext);
            strategyEvaluation.addNeedEvaluation(requirement, requirementEvaluation);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void meetsDemands(final StrategyEvaluation strategyEvaluation, final Strategy strategy, final EvaluationContext evaluationContext) {
        final Set<Requirement> requirements = strategy.getDemands();
        for (final Requirement requirement : requirements) {
            final RequirementEvaluation requirementEvaluation = requirementEvaluatorManager.evaluate(requirement, evaluationContext);
            strategyEvaluation.addDemandEvaluation(requirement, requirementEvaluation);
        }
    }

    public ExtractionContext createContext(final StrategyEvaluation strategyEvaluation, final Strategy strategy, final EvaluationContext evaluationContext) {
        final ExtractionContext context = (ExtractionContext) create(strategy.getExtractionContextClass());

        final Set<Requirement> needRequirements = strategy.getNeeds();
        for (final Requirement requirement : needRequirements) {
            final RequirementEvaluation requirementEvaluation = strategyEvaluation.getNeedEvaluation(requirement);
            final ExtractionContextAction action = strategy.getNeedAction(requirement);
            action.perform(context, requirementEvaluation.value);
        }

        final Set<Requirement> demandRequirements = strategy.getDemands();
        for (final Requirement requirement : demandRequirements) {
            final RequirementEvaluation requirementEvaluation = strategyEvaluation.getDemandEvaluation(requirement);
            final ExtractionContextAction action = strategy.getDemandAction(requirement);
            action.perform(context, requirementEvaluation.value);
        }

        return context;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Extraction execute(final Strategy strategy, final ExtractionContext context) {
        Extractor extractor = null;
        for (final Extractor possibleExtractor : autowiredExtractors) {
            if (possibleExtractor.getClass().equals(strategy.getExtractorClass())) {
                extractor = possibleExtractor;
            }
        }

        if (extractor == null) {
            extractor = (Extractor) create(strategy.getExtractorClass());;
        }

        Extraction result;
        try {
            result = extractor.extract(context);
        } catch (final Exception e) {
            result = new Extraction(ExtractionResult.Exception, e);
        }
        return result;
    }

    private <T> T create(final Class<T> clazz) {
        Constructor<T> constructor;
        try {
            constructor = clazz.getConstructor();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        T instance;
        try {
            instance = constructor.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

}
