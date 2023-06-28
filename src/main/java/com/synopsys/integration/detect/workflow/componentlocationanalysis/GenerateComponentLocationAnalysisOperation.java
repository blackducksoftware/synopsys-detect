package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.file.DetectFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class will generate the appropriate input file for Component Locator, invoke the library's JAR and
 * saves the resulting output file in the appropriate output subdirectory.
 */
public class GenerateComponentLocationAnalysisOperation {
    private static final String COMPONENT_LOCATOR_INPUT_FILE_NAME = "components-source.json";
    private static final String DETECT_OUTPUT_FILE_NAME = "components-with-locations.json";
    private final BdioToComponentListTransformer bdioTransformer = new BdioToComponentListTransformer();
    private final ScanResultToComponentListTransformer scanResultTransformer = new ScanResultToComponentListTransformer();


    public File locateComponentsForNonPersistentOnlineDetectorScan(List<DeveloperScansScanView> rapidFullResults, File scanOutputFolder, File sourceDir) throws DetectUserFriendlyException {
        // In Part II:
            // given a rapid scan full result response, call ScanResultToCLLComponentTransformer to get CLL input file (components-source.json)
        List<Component> componentsList = scanResultTransformer.transformScanResultToComponentList(rapidFullResults);

        File componentLocatorInput = generateComponentLocatorInput(componentsList, scanOutputFolder, sourceDir);
        return callComponentLocator(componentLocatorInput, scanOutputFolder);
    }

    public File locateComponentsForOfflineDetectorScan(BdioResult bdio, File scanOutputFolder, File sourceDir) throws DetectUserFriendlyException {
        List<Component> componentsList = bdioTransformer.transformBdioToComponentList(bdio);
        File componentLocatorInput = generateComponentLocatorInput(componentsList, scanOutputFolder, sourceDir); // TOME I guess we dont need the scan output folder here since the input file can be anywhere
        return callComponentLocator(componentLocatorInput, scanOutputFolder);
        // call library w/ CLL input file and return that, but for now just return the input file as is.
    }

    private File generateComponentLocatorInput(List<Component> componentsList, File scanOutputFolder, File sourceDir) throws DetectUserFriendlyException {
        ComponentLocatorInput componentLocatorInputObject = new ComponentLocatorInput(sourceDir.getAbsolutePath(), new Metadata(), componentsList);
        return serializeInputToJson(scanOutputFolder, componentLocatorInputObject);
    }

    private File callComponentLocator(File inputFile, File scanOutputFolder) {
        String[] args = {inputFile.getAbsolutePath(), scanOutputFolder.toString()}; // TOME one or the other
        // TODO maybe we should call the lib with desired path where we want the file saved ? And then after checking success status we can return that expected file as is?
        // get class from jar and call it with the args
        return inputFile;
    }

    private File serializeInputToJson(File saveInputFileDir, ComponentLocatorInput libInput) throws DetectUserFriendlyException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serializedLibInput = gson.toJson(libInput);
        try {
            File componentsWithLocations =  new File (saveInputFileDir, COMPONENT_LOCATOR_INPUT_FILE_NAME);
            DetectFileUtils.writeToFile(componentsWithLocations, serializedLibInput);
            return componentsWithLocations;
        } catch (IOException ex) {
            throw new DetectUserFriendlyException("Failed to create component location analysis output file", ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }

    // todo: method to eventually clean up components-source.json file
}
