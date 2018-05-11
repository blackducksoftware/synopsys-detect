package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@SuppressWarnings("rawtypes")
public abstract class Strategy<C extends ExtractionContext, E extends Extractor<C>>  {

    private final String name;
    private final BomToolType bomToolType;

    private final Class<C> extractionContextClass;
    private final Class<E> extractorClass;

    private final Set<Strategy> yieldsToStrategies = new HashSet<>();
    private final StrategySearchOptions searchOptions = new StrategySearchOptions(0, false);

    public Strategy(final String name, final BomToolType bomToolType, final Class<C> extractionContextClass, final Class<E> extractorClass) {
        this.name = name;
        this.bomToolType = bomToolType;
        this.extractionContextClass = extractionContextClass;
        this.extractorClass = extractorClass;
    }

    public void yieldsTo(final Strategy strategy) {
        yieldsToStrategies.add(strategy);
    }

    public abstract Applicable applicable(final EvaluationContext evaluation, final C context);
    public abstract Extractable extractable(final EvaluationContext evaluation, final C context);

    public String getName() {
        return name;
    }

    public BomToolType getBomToolType() {
        return bomToolType;
    }

    public Class<C> getExtractionContextClass() {
        return extractionContextClass;
    }

    public Class<E> getExtractorClass() {
        return extractorClass;
    }

    public Set<Strategy> getYieldsToStrategies() {
        return yieldsToStrategies;
    }

    public StrategySearchOptions getSearchOptions() {
        return searchOptions;
    }

    public ExtractionContext createContext() {
        return create(getExtractionContextClass());
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
