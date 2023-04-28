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

/**
 * This class represents the point at which Detect will call the Component Locator Library.
 * It is only a placeholder that generates an empty file for now.
 */
public class ComponentLocationAnalysisReporter {

    public static File generateFileForNonPersistentPkgMngrScan(List<DeveloperScansScanView> rapidFullResults, DirectoryManager dm) throws DetectUserFriendlyException {
        File componentsWithLocations =  new File (dm.getScanOutputDirectory(), "components_with_locations.json");
        return generatePlaceHolderJson(componentsWithLocations);
    } // see test case: RapidModeGenerateJsonOperation.java
    // also see above if you have time to change this file to include what will be sent to the library !!!! and add a lovely lil metadata field

    public static File generateFileForOfflinePkgMngrScan(BdioResult bdioPkgMngrResults, DirectoryManager dm) throws DetectUserFriendlyException {
        File componentsWithLocation = new File (dm.getScanOutputDirectory(), "components_with_locations.json");
        return generatePlaceHolderJson(componentsWithLocation);
    }

    private static File generatePlaceHolderJson(File componentsWithLocations) throws DetectUserFriendlyException {
        try {
            DetectFileUtils.writeToFile(componentsWithLocations, "{}");
        } catch (IOException ex) {
            throw new DetectUserFriendlyException("Cannot create components with locations report file", ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
        return componentsWithLocations;
    }
}
