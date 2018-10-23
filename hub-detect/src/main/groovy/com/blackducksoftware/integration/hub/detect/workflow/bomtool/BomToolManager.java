package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.event.Event;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationResult;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionManager;
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

    public BomToolManager(SearchManager searchManager, ExtractionManager extractionManager, PreparationManager preparationManager, ProjectNameVersionManager projectNameVersionManager, EventSystem eventSystem) {
        this.searchManager = searchManager;
        this.extractionManager = extractionManager;
        this.preparationManager = preparationManager;
        this.eventSystem = eventSystem;
    }

    public BomToolResult runBomTools() {
        List<BomToolEvaluation> bomToolEvaluations = new ArrayList<>();

        //search
        SearchResult searchResult = searchManager.performSearch();
        eventSystem.publishEvent(Event.SearchCompleted, searchResult);
        bomToolEvaluations.addAll(searchResult.getBomToolEvaluations());

        //prepare
        PreparationResult preparationResult = preparationManager.prepareExtractions(bomToolEvaluations);
        eventSystem.publishEvent(Event.PreparationCompleted, preparationResult);

        //extract
        ExtractionResult extractionResult = extractionManager.performExtractions(bomToolEvaluations);
        eventSystem.publishEvent(Event.ExtractionCompleted, extractionResult);

        //create results
        BomToolResult bomToolResult = new BomToolResult();
        bomToolResult.evaluatedBomTools = bomToolEvaluations;
        bomToolResult.bomToolCodeLocations = extractionResult.getDetectCodeLocations();

        bomToolResult.failedBomToolGroupTypes.addAll(preparationResult.getFailedBomToolTypes());
        bomToolResult.failedBomToolGroupTypes.addAll(extractionResult.getFailedBomToolTypes());

        bomToolResult.succesfullBomToolGroupTypes.addAll(preparationResult.getSuccessfulBomToolTypes());
        bomToolResult.succesfullBomToolGroupTypes.addAll(extractionResult.getSuccessfulBomToolTypes());
        bomToolResult.succesfullBomToolGroupTypes.removeIf(it -> bomToolResult.failedBomToolGroupTypes.contains(it));

        //post status
        Map<BomToolGroupType, StatusType> bomToolStatus = new HashMap<>();
        bomToolResult.succesfullBomToolGroupTypes.forEach(it -> bomToolStatus.put(it, StatusType.SUCCESS));
        bomToolResult.failedBomToolGroupTypes.forEach(it -> bomToolStatus.put(it, StatusType.FAILURE));
        bomToolStatus.forEach((bomTool, status) -> eventSystem.publishEvent(Event.StatusSummary, new DetectorStatus(bomTool, status)));

        return bomToolResult;
    }
}
