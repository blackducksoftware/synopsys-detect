package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.tool.detector.DetectorToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectorStatus;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;

public class DetectorManager {

    SearchManager searchManager;
    PreparationManager preparationManager;
    ExtractionManager extractionManager;
    EventSystem eventSystem;

    public DetectorManager(SearchManager searchManager, ExtractionManager extractionManager, PreparationManager preparationManager, EventSystem eventSystem) {
        this.searchManager = searchManager;
        this.extractionManager = extractionManager;
        this.preparationManager = preparationManager;
        this.eventSystem = eventSystem;
    }

    public DetectorToolResult runDetectors() {
        List<DetectorEvaluation> detectorEvaluations = new ArrayList<>();

        //search
        SearchResult searchResult = searchManager.performSearch();
        eventSystem.publishEvent(Event.SearchCompleted, searchResult);
        detectorEvaluations.addAll(searchResult.getDetectorEvaluations());

        //prepare
        PreparationResult preparationResult = preparationManager.prepareExtractions(detectorEvaluations);
        eventSystem.publishEvent(Event.PreparationsCompleted, preparationResult);

        //extract
        ExtractionResult extractionResult = extractionManager.performExtractions(detectorEvaluations);
        eventSystem.publishEvent(Event.ExtractionsCompleted, extractionResult);

        //create results
        DetectorToolResult detectorToolResult = new DetectorToolResult();
        detectorToolResult.evaluatedBomTools = detectorEvaluations;
        detectorToolResult.bomToolCodeLocations = extractionResult.getDetectCodeLocations();
        detectorToolResult.applicableDetectorTypes = searchResult.getApplicableBomTools();

        detectorToolResult.failedDetectorTypes.addAll(preparationResult.getFailedBomToolTypes());
        detectorToolResult.failedDetectorTypes.addAll(extractionResult.getFailedBomToolTypes());

        detectorToolResult.succesfullDetectorTypes.addAll(preparationResult.getSuccessfulBomToolTypes());
        detectorToolResult.succesfullDetectorTypes.addAll(extractionResult.getSuccessfulBomToolTypes());
        detectorToolResult.succesfullDetectorTypes.removeIf(it -> detectorToolResult.failedDetectorTypes.contains(it));

        //post status
        Map<DetectorType, StatusType> bomToolStatus = new HashMap<>();
        detectorToolResult.succesfullDetectorTypes.forEach(it -> bomToolStatus.put(it, StatusType.SUCCESS));
        detectorToolResult.failedDetectorTypes.forEach(it -> bomToolStatus.put(it, StatusType.FAILURE));
        bomToolStatus.forEach((bomTool, status) -> eventSystem.publishEvent(Event.StatusSummary, new DetectorStatus(bomTool, status)));

        return detectorToolResult;
    }
}
