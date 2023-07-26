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
    public static final String OPERATION_NAME = "Generating Component Location Analysis File for All Components";
    public static final String DETECT_OUTPUT_FILE_NAME = "components-with-locations.json";
    public static final String SUPPORTED_DETECTORS_LOG_MSG = "Component Location Analysis supports NPM, Maven, Gradle and NuGet detectors only.";
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

    public void locateComponentsForOnlineIntelligentScan() throws ComponentLocatorException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        logger.info("Intelligent Scan mode does not support Component Location Analysis.");
        failComponentLocationAnalysisOperation();
    }

    /**
     * A ComponentLocatorException shoud be thrown in all cases where the output file is not created so that the
     * appropriate status can be logged for the requested {@link GenerateComponentLocationAnalysisOperation}.
     */
    public void failComponentLocationAnalysisOperation() throws ComponentLocatorException {
        throw new ComponentLocatorException("Failed to generate Component Location Analysis file.");
    }

    private void runComponentLocator(List<Component> componentsList, File scanOutputFolder, File projectSrcDir) throws ComponentLocatorException {
        Input componentLocatorInput = generateComponentLocatorInput(componentsList, projectSrcDir);
        String outputFilepath = scanOutputFolder + "/" + DETECT_OUTPUT_FILE_NAME;

        logger.info(ReportConstants.RUN_SEPARATOR);
        int status = ComponentLocator.locateComponents(componentLocatorInput, outputFilepath);
        if (status != 0) {
            logger.warn("Component Locator execution has failed.");
            logger.info(ReportConstants.RUN_SEPARATOR);
            failComponentLocationAnalysisOperation();
        }
        logger.info("Component Location Analysis file saved at: {}", outputFilepath);
        logger.info(ReportConstants.RUN_SEPARATOR);
    }

    private Input generateComponentLocatorInput(List<Component> componentsList, File sourceDir) {
        return new Input(sourceDir.getAbsolutePath(), new JsonObject(), componentsList);
    }
}
