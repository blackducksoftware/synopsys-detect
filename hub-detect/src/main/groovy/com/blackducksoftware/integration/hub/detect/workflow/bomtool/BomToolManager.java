package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationResult;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.synopsys.integration.util.NameVersion;

public class BomToolManager {

    SearchManager searchManager;
    PreparationManager preparationManager;
    ExtractionManager extractionManager;
    ProjectVersionManager projectVersionManager;
    ReportManager reportManager;
    PhoneHomeManager phoneHomeManager;

    public BomToolManager(SearchManager searchManager, ExtractionManager extractionManager, ProjectVersionManager projectVersionManager, ReportManager reportManager, PhoneHomeManager phoneHomeManager,
        PreparationManager preparationManager) {
        this.searchManager = searchManager;
        this.extractionManager = extractionManager;
        this.projectVersionManager = projectVersionManager;
        this.reportManager = reportManager;
        this.phoneHomeManager = phoneHomeManager;
        this.preparationManager = preparationManager;

    }

    public BomToolResult runBomTools() {
        List<BomToolEvaluation> bomToolEvaluations = new ArrayList<>();

        SearchResult searchResult = searchManager.performSearch();

        bomToolEvaluations.addAll(searchResult.getBomToolEvaluations());
        reportManager.searchCompleted(bomToolEvaluations);

        // we've gone through all applicable bom tools so we now have the complete metadata to phone home
        phoneHomeManager.startPhoneHome(searchResult.getApplicableBomTools());

        //prepare
        PreparationResult preparationResult = preparationManager.prepareExtractions(bomToolEvaluations);
        reportManager.preparationCompleted(bomToolEvaluations);

        //extract
        ExtractionResult extractionResult = extractionManager.performExtractions(bomToolEvaluations);
        reportManager.extractionsCompleted(bomToolEvaluations);

        //name.version
        final NameVersion nameVersion = projectVersionManager.evaluateProjectNameVersion(bomToolEvaluations);

        //donezo.
        BomToolResult bomToolResult = new BomToolResult();
        bomToolResult.bomToolCodeLocations = extractionResult.getDetectCodeLocations();
        bomToolResult.bomToolProjectInfo = nameVersion;

        bomToolResult.succesfullBomToolGroupTypes.addAll(preparationResult.getSuccessfulBomToolTypes());
        bomToolResult.succesfullBomToolGroupTypes.addAll(extractionResult.getSuccessfulBomToolTypes());

        bomToolResult.failedBomToolGroupTypes.addAll(preparationResult.getFailedBomToolTypes());
        bomToolResult.failedBomToolGroupTypes.addAll(extractionResult.getFailedBomToolTypes());

        bomToolResult.succesfullBomToolGroupTypes.removeIf(it -> bomToolResult.failedBomToolGroupTypes.contains(it));

        return bomToolResult;
    }

}
