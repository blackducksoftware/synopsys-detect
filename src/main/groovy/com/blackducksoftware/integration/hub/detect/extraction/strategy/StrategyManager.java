package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.bucket.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.Evaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.Evaluation.EvaluationResult;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluatorManager;

public class StrategyManager {

    @Autowired
    public List<StrategyProvider> strategyProviders;

    @Autowired
    public EvaluatorManager evaluatorManager;

    private final List<Strategy<?, ?>> strategies = new ArrayList<>();

    public List<Strategy<?, ?>> findApplicableStrategies(final File directory) {
        //Setup the evaluation context.
        final EvaluationContext evalContext = new EvaluationContext(directory);

        //Loop through all the strategies.
        return strategies.stream().filter(it -> doesStrategyApply(it, evalContext)).collect(Collectors.toList());

    }

    @SuppressWarnings("unchecked")
    public  <C extends ExtractionContext, E extends Extractor> void executeStrategy(final Strategy<C, E> strategy, final File directory) {
        final EvaluationContext evalContext = new EvaluationContext(directory);
        final C extractionContext = (C) createContext(strategy.extractionContextClass);

        for (final Requirement<C,?> requirement : strategy.requirements) {
            evalRequirement(requirement, evalContext, extractionContext);
        }

    }

    @SuppressWarnings("unchecked")
    private <C extends ExtractionContext, V> void evalRequirement(final Requirement<C, V> requirement, final EvaluationContext evalContext,  final C extractionContext) {
        final Evaluation<V> evaluation = (Evaluation<V>) evaluatorManager.evaluate(requirement, evalContext);
        if (evaluation.result == EvaluationResult.Passed) {
            applyRequirement(evaluation, requirement, extractionContext);
        }
    }

    private <C extends ExtractionContext, V> void applyRequirement(final Evaluation<V> evaluation, final Requirement<C, V> requirement,  final C extractionContext) {
        requirement.action.perform(extractionContext, evaluation.value);
    }

    private boolean doesStrategyApply(final Strategy<?,?> strategy, final EvaluationContext evalContext) {
        boolean strategyApplies = true;
        for (final Requirement<?,?> requirement : strategy.requirements) {
            final Evaluation<?> evaluation = evaluatorManager.evaluate(requirement, evalContext);
            strategyApplies = strategyApplies && evaluation.result == EvaluationResult.Passed;
        }
        return strategyApplies;
    }

    public void executeStrategy() {
        for (final Strategy<?,?> strategy : strategies) {

        }
    }

    private <T extends ExtractionContext> ExtractionContext createContext(final Class<T> clazz) {
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
