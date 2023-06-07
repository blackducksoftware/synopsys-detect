package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.file.DetectFileUtils;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GenerateComponentLocationAnalysisOperation {
    private static final String CLL_INPUT_FILE_NAME = "components-source.json";
    private static final String CLL_OUTPUT_FILE_NAME = "components-with-locations.json";
    private final BdioToComponentLocatorInputTransformer bdioTransformer;
    private final ScanResultToComponentLocatorInputTransformer scanResultTransformer;

    public GenerateComponentLocationAnalysisOperation(BdioToComponentLocatorInputTransformer bdioTransformer, ScanResultToComponentLocatorInputTransformer scanResultTransformer) {
        this.bdioTransformer = bdioTransformer;
        this.scanResultTransformer = scanResultTransformer;
    }

    public static File forNonPersistentOnlinePkgMngrScan(List<DeveloperScansScanView> rapidFullResults, File scanOutputFolder) throws DetectUserFriendlyException {
        // In Part II:
            // transform rapid scan results -> CLL input
            // call library w/ CLL input
        return generatePlaceHolderJsonFileForNow(scanOutputFolder);
    }

    public static File forOfflinePkgMngrScan(BdioResult bdio, File scanOutputFolder) throws DetectUserFriendlyException {
        // In Part II:
            // transform BDIO -> CLL input
            // call library w/ CLL input
        return generatePlaceHolderJsonFileForNow(scanOutputFolder);
    }

    private static File generatePlaceHolderJsonFileForNow(File scanOutputFolder) throws DetectUserFriendlyException {
        try {
            File componentsWithLocations =  new File (scanOutputFolder, CLL_OUTPUT_FILE_NAME);
            DetectFileUtils.writeToFile(componentsWithLocations, "{}");
            return componentsWithLocations;
        } catch (IOException ex) {
            throw new DetectUserFriendlyException("Failed to create component location analysis output file", ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }

    // todo: method to eventually clean up components-source.json file
}
