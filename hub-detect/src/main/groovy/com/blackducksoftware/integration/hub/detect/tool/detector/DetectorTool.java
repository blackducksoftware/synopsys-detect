package com.blackducksoftware.integration.hub.detect.tool.detector;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolFactory;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolEvaluationNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;
import com.synopsys.integration.util.NameVersion;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectContext detectContext;

    public DetectorTool(DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public DetectorToolResult performBomTools(SearchOptions searchOptions, String projectBomTool) {
        logger.info("Preparing to initialize bom tools.");
        BomToolFactory bomToolFactory = detectContext.getBean(BomToolFactory.class);
        EventSystem eventSystem = detectContext.getBean(EventSystem.class);

        logger.info("Building bom tool system.");
        BomToolSearchProvider bomToolSearchProvider = new BomToolSearchProvider(bomToolFactory);
        BomToolSearchEvaluator bomToolSearchEvaluator = new BomToolSearchEvaluator();

        SearchManager searchManager = new SearchManager(searchOptions, bomToolSearchProvider, bomToolSearchEvaluator, eventSystem);
        PreparationManager preparationManager = new PreparationManager(eventSystem);
        ExtractionManager extractionManager = new ExtractionManager();

        BomToolManager bomToolManager = new BomToolManager(searchManager, extractionManager, preparationManager, eventSystem);
        logger.info("Running bom tools.");
        DetectorToolResult detectorToolResult = bomToolManager.runBomTools();
        logger.info("Finished running bom tools.");
        eventSystem.publishEvent(Event.BomToolsComplete, detectorToolResult);

        BomToolEvaluationNameVersionDecider bomToolEvaluationNameVersionDecider = new BomToolEvaluationNameVersionDecider(new BomToolNameVersionDecider());
        Optional<NameVersion> bomToolNameVersion = bomToolEvaluationNameVersionDecider.decideSuggestion(detectorToolResult.evaluatedBomTools, projectBomTool);
        detectorToolResult.bomToolProjectNameVersion = bomToolNameVersion;

        return detectorToolResult;
    }
}
