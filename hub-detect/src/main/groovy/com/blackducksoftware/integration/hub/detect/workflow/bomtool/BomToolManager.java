package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectorStatus;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;

public class BomToolManager {

    SearchManager searchManager;
    PreparationManager preparationManager;
    ExtractionManager extractionManager;
    EventSystem eventSystem;

    public BomToolManager(SearchManager searchManager, ExtractionManager extractionManager, PreparationManager preparationManager, EventSystem eventSystem) {
        this.searchManager = searchManager;
        this.extractionManager = extractionManager;
        this.preparationManager = preparationManager;
        this.eventSystem = eventSystem;
    }

    public BomToolsResult runBomTools() {
        List<BomToolEvaluation> bomToolEvaluations = new ArrayList<>();

        //search
        SearchResult searchResult = searchManager.performSearch();
        eventSystem.publishEvent(Event.SearchCompleted, searchResult);
        bomToolEvaluations.addAll(searchResult.getBomToolEvaluations());

        //prepare
        PreparationResult preparationResult = preparationManager.prepareExtractions(bomToolEvaluations);
        eventSystem.publishEvent(Event.PreparationsCompleted, preparationResult);

        //extract
        ExtractionResult extractionResult = extractionManager.performExtractions(bomToolEvaluations);
        eventSystem.publishEvent(Event.ExtractionsCompleted, extractionResult);

        //create results
        BomToolsResult bomToolsResult = new BomToolsResult();
        bomToolsResult.evaluatedBomTools = bomToolEvaluations;
        bomToolsResult.bomToolCodeLocations = extractionResult.getDetectCodeLocations();

        bomToolsResult.failedBomToolGroupTypes.addAll(preparationResult.getFailedBomToolTypes());
        bomToolsResult.failedBomToolGroupTypes.addAll(extractionResult.getFailedBomToolTypes());

        bomToolsResult.succesfullBomToolGroupTypes.addAll(preparationResult.getSuccessfulBomToolTypes());
        bomToolsResult.succesfullBomToolGroupTypes.addAll(extractionResult.getSuccessfulBomToolTypes());
        bomToolsResult.succesfullBomToolGroupTypes.removeIf(it -> bomToolsResult.failedBomToolGroupTypes.contains(it));

        //post status
        Map<BomToolGroupType, StatusType> bomToolStatus = new HashMap<>();
        bomToolsResult.succesfullBomToolGroupTypes.forEach(it -> bomToolStatus.put(it, StatusType.SUCCESS));
        bomToolsResult.failedBomToolGroupTypes.forEach(it -> bomToolStatus.put(it, StatusType.FAILURE));
        bomToolStatus.forEach((bomTool, status) -> eventSystem.publishEvent(Event.StatusSummary, new DetectorStatus(bomTool, status)));

        return bomToolsResult;
    }
}
