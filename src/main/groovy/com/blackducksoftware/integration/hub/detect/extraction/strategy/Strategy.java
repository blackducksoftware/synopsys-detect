package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContextAction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@SuppressWarnings("rawtypes")
public class Strategy<C extends ExtractionContext, E extends Extractor<C>>  {

    private final String name;
    private final BomToolType bomToolType;

    protected Map<Requirement, ExtractionContextAction> needActionMap = new HashMap<>();
    private final Map<Requirement, ExtractionContextAction> demandActionMap = new HashMap<>();

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

    public void needsFile(final String filepattern, final ExtractionContextAction<C, File> action) {

    }

    public void demandsStandardExecutable(final StandardExecutableType type, final ExtractionContextAction<C, File> action ) {

    }


    public FileRequirement needsFile(final String filepattern)//, ExtractionContextAction<C,V> action)
    {
        return new FileRequirement();
    }

    public void addDemand(final Requirement requirement, final ExtractionContextAction contextAction) {
        demandActionMap.put(requirement, contextAction);
    }

    public void addNeed(final Requirement requirement, final ExtractionContextAction contextAction) {
        needActionMap.put(requirement, contextAction);
    }

    public void yieldsTo(final Strategy strategy) {
        yieldsToStrategies.add(strategy);
    }


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

    public StrategySearchOptions getSearchOptions() {
        return searchOptions;
    }
}
