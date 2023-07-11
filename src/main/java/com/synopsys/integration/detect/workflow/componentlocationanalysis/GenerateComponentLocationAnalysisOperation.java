package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.file.DetectFileUtils;
import com.synopsys.integration.fixpr.generic.Application;
import com.synopsys.integration.fixpr.generic.beans.Component;
import com.synopsys.integration.fixpr.generic.beans.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
     * @param sourceDir source directory of project being scanned
     * @throws DetectUserFriendlyException
     */
    public void locateComponentsForNonPersistentOnlineDetectorScan(List<DeveloperScansScanView> rapidFullResults, File scanOutputFolder, File sourceDir) throws DetectUserFriendlyException {
        List<Component> componentsList = scanResultTransformer.transformScanResultToComponentList(rapidFullResults);
        Input componentLocatorInput = generateComponentLocatorInput(componentsList, sourceDir);
        serializeInputToJson(scanOutputFolder, componentLocatorInput); // TODO remove me before merging to master
        callComponentLocatorObfuscatedJar(componentLocatorInput, scanOutputFolder);
    }

    /**
     * Given a BDIO, generates an output file consisting of the list of unique components detected and their declaration
     * locations.
     * @param bdio from running offline Detector scan
     * @param scanOutputFolder Detect's output subdirectory where this file will be saved
     * @param sourceDir source directory of project being scanned
     * @throws DetectUserFriendlyException
     */
    public void locateComponentsForOfflineDetectorScan(BdioResult bdio, File scanOutputFolder, File sourceDir) throws DetectUserFriendlyException {
        List<Component> componentsList = bdioTransformer.transformBdioToComponentList(bdio);
        Input componentLocatorInput = generateComponentLocatorInput(componentsList, sourceDir);
        serializeInputToJson(scanOutputFolder, componentLocatorInput); //TODO remove me before merging to master
        callComponentLocatorObfuscatedJar(componentLocatorInput, scanOutputFolder);
    }

    private Input generateComponentLocatorInput(List<Component> componentsList, File sourceDir) {
        return new Input(sourceDir.getAbsolutePath(), new JsonObject(), componentsList);
    }

    private void callComponentLocatorObfuscatedJar(Input componentLocatorInputObj, File scanOutputFolder) {
        try {
            int status = Application.locateComponents(componentLocatorInputObj, scanOutputFolder.toString() + "/" + DETECT_OUTPUT_FILE_NAME);
            if (status != 0) {
                logger.info("There was a problem during Component Locator execution. Failed to generate Component Location Analysis file.");
            }
        } catch (Exception e) {
            logger.info("There was a problem during Component Locator execution. Failed to generate Component Location Analysis file.");
        }
    }

    private File serializeInputToJson(File saveInputFileDir, Input libInput) throws DetectUserFriendlyException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serializedLibInput = gson.toJson(libInput);
        try {
            File componentsWithLocations =  new File (saveInputFileDir, "components-source.json");
            DetectFileUtils.writeToFile(componentsWithLocations, serializedLibInput);
            return componentsWithLocations;
        } catch (IOException ex) {
            throw new DetectUserFriendlyException("Failed to create Component Locator input file", ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }
}
