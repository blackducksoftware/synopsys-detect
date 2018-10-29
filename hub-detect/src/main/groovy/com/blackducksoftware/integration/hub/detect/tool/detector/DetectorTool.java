package com.blackducksoftware.integration.hub.detect.tool.detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.blackducksoftware.integration.hub.detect.BomToolBeanConfiguration;
import com.blackducksoftware.integration.hub.detect.BomToolDependencies;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolFactory;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolsResult;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectRun detectRun;
    private final BomToolDependencies bomToolDependencies;
    private final EventSystem eventSystem;

    public DetectorTool(DetectRun detectRun, BomToolDependencies bomToolDependencies, EventSystem eventSystem) {
        this.detectRun = detectRun;
        this.bomToolDependencies = bomToolDependencies;
        this.eventSystem = eventSystem;
    }

    public BomToolsResult performBomTools(SearchOptions searchOptions) {

        logger.info("Preparing to initialize bom tools.");
        AnnotationConfigApplicationContext runContext = new AnnotationConfigApplicationContext();
        runContext.setDisplayName("Detect Bom Tools " + detectRun.getRunId());
        runContext.register(BomToolBeanConfiguration.class);
        runContext.registerBean(BomToolDependencies.class, () -> bomToolDependencies);
        runContext.refresh();
        logger.info("Bom tools initialized. Retrieving bom tool factory.");
        BomToolFactory bomToolFactory = runContext.getBean(BomToolFactory.class);

        logger.info("Building bom tool system.");
        BomToolSearchProvider bomToolSearchProvider = new BomToolSearchProvider(bomToolFactory);
        BomToolSearchEvaluator bomToolSearchEvaluator = new BomToolSearchEvaluator();

        SearchManager searchManager = new SearchManager(searchOptions, bomToolSearchProvider, bomToolSearchEvaluator, eventSystem);
        PreparationManager preparationManager = new PreparationManager(eventSystem);
        ExtractionManager extractionManager = new ExtractionManager();

        BomToolManager bomToolManager = new BomToolManager(searchManager, extractionManager, preparationManager, eventSystem);
        logger.info("Running bom tools.");
        BomToolsResult bomToolsResult = bomToolManager.runBomTools();
        logger.info("Finished running bom tools.");
        return bomToolsResult;
    }
}
