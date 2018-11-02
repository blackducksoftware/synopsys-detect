package com.blackducksoftware.integration.hub.detect.tool.detector;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.DetectorFactory;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.DetectorManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectorEvaluationNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectorNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.DetectorSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.DetectorSearchProvider;
import com.synopsys.integration.util.NameVersion;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectContext detectContext;

    public DetectorTool(DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public DetectorToolResult performDetectors(SearchOptions searchOptions, String projectBomTool) {
        logger.info("Preparing to initialize detectors.");
        DetectorFactory detectorFactory = detectContext.getBean(DetectorFactory.class);
        EventSystem eventSystem = detectContext.getBean(EventSystem.class);

        logger.info("Building detector system.");
        DetectorSearchProvider detectorSearchProvider = new DetectorSearchProvider(detectorFactory);
        DetectorSearchEvaluator detectorSearchEvaluator = new DetectorSearchEvaluator();

        SearchManager searchManager = new SearchManager(searchOptions, detectorSearchProvider, detectorSearchEvaluator, eventSystem);
        PreparationManager preparationManager = new PreparationManager(eventSystem);
        ExtractionManager extractionManager = new ExtractionManager();

        DetectorManager detectorManager = new DetectorManager(searchManager, extractionManager, preparationManager, eventSystem);
        logger.info("Running detectors.");
        DetectorToolResult detectorToolResult = detectorManager.runDetectors();
        logger.info("Finished running detectors.");
        eventSystem.publishEvent(Event.BomToolsComplete, detectorToolResult);

        DetectorEvaluationNameVersionDecider detectorEvaluationNameVersionDecider = new DetectorEvaluationNameVersionDecider(new DetectorNameVersionDecider());
        Optional<NameVersion> bomToolNameVersion = detectorEvaluationNameVersionDecider.decideSuggestion(detectorToolResult.evaluatedBomTools, projectBomTool);
        detectorToolResult.bomToolProjectNameVersion = bomToolNameVersion;

        return detectorToolResult;
    }
}
