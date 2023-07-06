package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.file.DetectFileUtils;
import com.synopsys.integration.fixpr.generic.Application;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class will generate the appropriate input file for Component Locator, invoke the library's obfuscated JAR and
 * save the resulting output file in the appropriate output subdirectory.
 */
public class GenerateComponentLocationAnalysisOperation {
    private static final String COMPONENT_LOCATOR_INPUT_FILE_NAME = "components-source.json"; // TODO input file name is irrelevant and temp, get rid of this
    public static final String DETECT_OUTPUT_FILE_NAME = "components-with-locations.json"; // TODO this file name should be provided to the library, lib should have no knowledge of this
    private final BdioToComponentListTransformer bdioTransformer = new BdioToComponentListTransformer();
    private final ScanResultToComponentListTransformer scanResultTransformer = new ScanResultToComponentListTransformer();


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
        File componentLocatorInput = generateComponentLocatorInput(componentsList, scanOutputFolder, sourceDir);
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
        File componentLocatorInput = generateComponentLocatorInput(componentsList, scanOutputFolder, sourceDir);
        callComponentLocatorObfuscatedJar(componentLocatorInput, scanOutputFolder);
    }

    private File generateComponentLocatorInput(List<Component> componentsList, File scanOutputFolder, File sourceDir) throws DetectUserFriendlyException {
        ComponentLocatorInput componentLocatorInputObject = new ComponentLocatorInput(sourceDir.getAbsolutePath(), new Metadata(), componentsList);
        return serializeInputToJson(scanOutputFolder, componentLocatorInputObject);
    }

    private void callComponentLocatorObfuscatedJar(File inputFile, File scanOutputFolder) {
        String[] args = {inputFile.getAbsolutePath(), scanOutputFolder.toString()};
        // TODO check success status, surround next line with try/catch
        Application.main(args);
        deleteInputFile(inputFile);
    }

    private File serializeInputToJson(File saveInputFileDir, ComponentLocatorInput libInput) throws DetectUserFriendlyException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serializedLibInput = gson.toJson(libInput);
        try {
            File componentsWithLocations =  new File (saveInputFileDir, COMPONENT_LOCATOR_INPUT_FILE_NAME);
            DetectFileUtils.writeToFile(componentsWithLocations, serializedLibInput);
            return componentsWithLocations;
        } catch (IOException ex) {
            // TODO change this to debug level log
            throw new DetectUserFriendlyException("Failed to create Component Locator input file", ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }

    private void deleteInputFile(File inputFile) {
        // TODO check success status
        inputFile.delete();
    }
}
