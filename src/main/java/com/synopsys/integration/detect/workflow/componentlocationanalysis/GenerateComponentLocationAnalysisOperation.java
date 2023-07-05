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
 * This class will generate the appropriate input file for Component Locator, invoke the library's JAR and
 * saves the resulting output file in the appropriate output subdirectory.
 */
public class GenerateComponentLocationAnalysisOperation {
    private static final String COMPONENT_LOCATOR_INPUT_FILE_NAME = "components-source.json";
    private static final String DETECT_OUTPUT_FILE_NAME = "components-with-locations.json";
    private final BdioToComponentListTransformer bdioTransformer = new BdioToComponentListTransformer();
    private final ScanResultToComponentListTransformer scanResultTransformer = new ScanResultToComponentListTransformer();


    public void locateComponentsForNonPersistentOnlineDetectorScan(List<DeveloperScansScanView> rapidFullResults, File scanOutputFolder, File sourceDir) throws DetectUserFriendlyException {
        List<Component> componentsList = scanResultTransformer.transformScanResultToComponentList(rapidFullResults);
        File componentLocatorInput = generateComponentLocatorInput(componentsList, scanOutputFolder, sourceDir);
        callComponentLocatorObfuscatedJar(componentLocatorInput, scanOutputFolder);
    }

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
        // TOME check success status
        // TODO also remember to blackduck.offline.mode.force.bdio: true set conditional so customer doesn't have to set this
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
            throw new DetectUserFriendlyException("Failed to create component location analysis output file", ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }

    private void deleteInputFile(File inputFile) {
        inputFile.delete(); // handle t/f
    }

    private File pretendLibOutputFile(File scanOutputDir) {
        try {
            File componentsWithLocations =  new File (scanOutputDir, DETECT_OUTPUT_FILE_NAME);
            DetectFileUtils.writeToFile(componentsWithLocations, "testing");
            return componentsWithLocations;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return new File(scanOutputDir, "testing and something went wrong");
    }
}
