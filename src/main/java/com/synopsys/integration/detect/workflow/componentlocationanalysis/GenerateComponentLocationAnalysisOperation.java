package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.file.DetectFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class will generate the appropriate input file for Component Locator, invoke the library's JAR and then
 * save the resulting output file in the appropriate output subdirectory.
 *
 * Will be fully implemented in a subsequent pull request to the Fix PR feature branch.
 */
public class GenerateComponentLocationAnalysisOperation {
    private static final String INPUT_FILE_NAME = "components-source.json";
    private static final String OUTPUT_FILE_NAME = "components-with-locations.json";
    private final BdioToComponentListTransformer bdioTransformer;
    private final ScanResultToComponentListTransformer scanResultTransformer;

    public GenerateComponentLocationAnalysisOperation(BdioToComponentListTransformer bdioTransformer, ScanResultToComponentListTransformer scanResultTransformer) {
        this.bdioTransformer = bdioTransformer;
        this.scanResultTransformer = scanResultTransformer;
    }

    /**
     * @param rapidFullResults
     * @param scanOutputFolder
     * @return
     * @throws DetectUserFriendlyException
     */
    public static File locateComponentsforNonPersistentOnlineDetectorScan(List<DeveloperScansScanView> rapidFullResults, File scanOutputFolder) throws DetectUserFriendlyException {
        // In Part II:
            // given a rapid scan full result response, call ScanResultToCLLComponentTransformer to get CLL input file (components-source.json)
            // call library w/ CLL input
            // return the resulting file (not anticipating any post-processing except saving it in the correct directory)
        return generatePlaceHolderJsonFileForNow(scanOutputFolder);
    }

    public static File locateComponentsForOfflineDetectorScan(BdioResult bdio, File scanOutputFolder) throws DetectUserFriendlyException {
        // In Part II:
            // given a BDIO, call BdioToCLLComponentTransformer to get CLL input file (components-source.json)
            // call library w/ CLL input
            // return the resulting file (not anticipating any post-processing except saving it in the correct directory)
        return generatePlaceHolderJsonFileForNow(scanOutputFolder);
    }

    /**
     * Placeholder file for testing purposes, saves a JSON file in the appropriate directory with the appropriate name.
     * @param scanOutputFolder
     * @return
     * @throws DetectUserFriendlyException
     */
    private static File generatePlaceHolderJsonFileForNow(File scanOutputFolder) throws DetectUserFriendlyException {
        try {
            File componentsWithLocations =  new File (scanOutputFolder, OUTPUT_FILE_NAME);
            DetectFileUtils.writeToFile(componentsWithLocations, "{}");
            return componentsWithLocations;
        } catch (IOException ex) {
            throw new DetectUserFriendlyException("Failed to create component location analysis output file", ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }

    // todo: method to eventually clean up components-source.json file
}
