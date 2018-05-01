package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContextAction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;

@SuppressWarnings("rawtypes")
public class Strategy<C extends ExtractionContext, E extends Extractor<C>>  {

    private String name;

    private final Map<Requirement, ExtractionContextAction> needActionMap;
    private final Map<Requirement, ExtractionContextAction> demandActionMap;

    private final Class<C> extractionContextClass;
    private final Class<E> extractorClass;

    private final Set<Strategy> yieldsToStrategies;

    public Strategy(final Map<Requirement, ExtractionContextAction> needActionMap, final Map<Requirement, ExtractionContextAction> demandActionMap, final Class<C> extractionContextClass, final Class<E> extractorClass, final Set<Strategy> yieldsToStrategies) {
        this.needActionMap = needActionMap;
        this.demandActionMap = demandActionMap;
        this.extractionContextClass = extractionContextClass;
        this.extractorClass = extractorClass;
        this.yieldsToStrategies = yieldsToStrategies;
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

    public Set<Requirement> getNeeds() {
        return needActionMap.keySet();
    }

    public Set<Requirement> getDemands() {
        return demandActionMap.keySet();
    }

    public ExtractionContextAction getNeedAction(final Requirement requirement) {
        return needActionMap.get(requirement);
    }

    public ExtractionContextAction getDemandAction(final Requirement requirement) {
        return demandActionMap.get(requirement);
    }
}
