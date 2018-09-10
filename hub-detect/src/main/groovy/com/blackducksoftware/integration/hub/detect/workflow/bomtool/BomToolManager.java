package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.factory.BomToolFactory;
import com.blackducksoftware.integration.hub.detect.factory.ExecutableFinderFactory;
import com.blackducksoftware.integration.hub.detect.factory.ExtractorFactory;
import com.blackducksoftware.integration.hub.detect.factory.InspectorManagerFactory;
import com.blackducksoftware.integration.hub.detect.workflow.boot.DetectContext;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.workflow.profiling.BomToolProfiler;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BomToolManager {

    SearchManager searchManager;
    ExtractionManager extractionManager;
    ProjectVersionManager projectVersionManager;
    public  BomToolManager(SearchManager searchManager, ExtractionManager extractionManager, ProjectVersionManager projectVersionManager) {

    }

    public BomToolResult runBomTools() {
        List<BomToolEvaluation> bomToolEvaluations = new ArrayList<>();
        try {
            final SearchResult searchResult = searchManager.performSearch();
            bomToolEvaluations.addAll(searchResult.getBomToolEvaluations());
        } catch (DetectUserFriendlyException e) {
            e.printStackTrace();
        }

        //extract
        List<DetectCodeLocation> codeLocations = new ArrayList<>();
        try {
            ExtractionResult extractionResult = extractionManager.performExtractions(bomToolEvaluations);
            codeLocations.addAll(extractionResult.getDetectCodeLocations());
        } catch (IntegrationException e) {
            e.printStackTrace();
        } catch (DetectUserFriendlyException e) {
            e.printStackTrace();
        }

        //finalize
        final NameVersion nameVersion = projectVersionManager.evaluateProjectNameVersion(bomToolEvaluations);

        //donezo.
        BomToolResult bomToolResult = new BomToolResult();
        bomToolResult.bomToolCodeLocations = codeLocations;
        bomToolResult.bomToolProjectInfo = nameVersion;
        return bomToolResult;
    }
}
