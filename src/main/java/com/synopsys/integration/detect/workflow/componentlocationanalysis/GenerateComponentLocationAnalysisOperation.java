package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.google.gson.JsonObject;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.componentlocator.ComponentLocator;
import com.synopsys.integration.componentlocator.beans.Component;
import com.synopsys.integration.componentlocator.beans.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;

import java.io.File;
import java.util.List;

/**
 * This class will generate the appropriate input file for Component Locator, invoke the library's obfuscated JAR and
 * save the resulting output file in the appropriate output subdirectory.
 */
public class GenerateComponentLocationAnalysisOperation {
    public static final String DETECT_OUTPUT_FILE_NAME = "components-with-locations.json";
    private final BdioToComponentListTransformer bdioTransformer = new BdioToComponentListTransformer();
    private final ScanResultToComponentListTransformer scanResultTransformer = new ScanResultToComponentListTransformer();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Given a Rapid/Stateless Detector scan result, generates an output file consisting of the list of reported
     * components, their corresponding policy violations/vulnerabilities and declaration locations.
     * @param rapidFullResults
     * @param scanOutputFolder Detect's output subdirectory where this file will be saved
     * @param projectSrcDir source directory of project being scanned
     * @throws DetectUserFriendlyException
     */
    public void locateComponentsForNonPersistentOnlineDetectorScan(List<DeveloperScansScanView> rapidFullResults, File scanOutputFolder, File projectSrcDir) throws ComponentLocatorException {
        List<Component> componentsList = scanResultTransformer.transformScanResultToComponentList(rapidFullResults);
        runComponentLocator(componentsList, scanOutputFolder, projectSrcDir);
    }

    /**
     * Given a BDIO, generates an output file consisting of the list of unique components detected and their declaration
     * locations.
     * @param bdio from running offline Detector scan
     * @param scanOutputFolder Detect's output subdirectory where this file will be saved
     * @param projectSrcDir source directory of project being scanned
     * @throws DetectUserFriendlyException
     */
    public void locateComponentsForOfflineDetectorScan(BdioResult bdio, File scanOutputFolder, File projectSrcDir) throws ComponentLocatorException {
        List<Component> componentsList = bdioTransformer.transformBdioToComponentList(bdio);
        runComponentLocator(componentsList, scanOutputFolder, projectSrcDir);
    }

    private void runComponentLocator(List<Component> componentsList, File scanOutputFolder, File projectSrcDir) throws ComponentLocatorException {
        Input componentLocatorInput = generateComponentLocatorInput(componentsList, projectSrcDir);
        logger.info(ReportConstants.RUN_SEPARATOR);
        logger.info("Running Component Locator.");
        String outputFile = scanOutputFolder + "/" + DETECT_OUTPUT_FILE_NAME;
        int status = ComponentLocator.locateComponents(componentLocatorInput, outputFile);
        if (status != 0) {
            logger.warn("Component Locator execution was unsuccessful. Enable debug level logging for details.");
            logger.info(ReportConstants.RUN_SEPARATOR);
            throw new ComponentLocatorException("Failed to generate Component Location Analysis file.");
        }
        logger.info("Component location analysis file saved at: {}", outputFile);
    }

    private Input generateComponentLocatorInput(List<Component> componentsList, File sourceDir) {
        return new Input(sourceDir.getAbsolutePath(), new JsonObject(), componentsList);
    }
}
