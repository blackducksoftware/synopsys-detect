package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContextAction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;

@SuppressWarnings("rawtypes")
public class Strategy<C extends ExtractionContext, E extends Extractor<C>>  {

    public Map<Requirement, ExtractionContextAction> requirementActionMap;
    public Map<Requirement, ExtractionContextAction> demandActionMap;

    public Class<C> extractionContextClass;
    public Class<E> extractorClass;

    public C newContext() {
        return create(extractionContextClass);
    }

    public E newExtractor() {
        return create(extractorClass);
    }

    public void require(final Requirement requirement, final ExtractionContextAction action) {
        requirementActionMap.put(requirement, action);
    }

    public Set<Requirement> getRequirements() {
        return requirementActionMap.keySet();
    }

    public ExtractionContextAction getAction(final Requirement requirement) {
        return requirementActionMap.get(requirement);
    }

    public void demand() {

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
