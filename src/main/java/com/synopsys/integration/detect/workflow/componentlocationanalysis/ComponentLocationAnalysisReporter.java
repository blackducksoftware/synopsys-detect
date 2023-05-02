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
 * This class is only a placeholder to represent the point at which Detect will call the Component Locator Library.
 * For now, it generates an empty components_with_locations.json file for testing purposes.
 */
public class ComponentLocationAnalysisReporter {

    public static File generateFileForNonPersistentPkgMngrScan(List<DeveloperScansScanView> rapidFullResults, DirectoryManager dm) throws DetectUserFriendlyException {
        File componentsWithLocations =  new File (dm.getScanOutputDirectory(), "components_with_locations.json");
        return generatePlaceHolderJsonFile(componentsWithLocations);
    }

    public static File generateFileForOfflinePkgMngrScan(BdioResult bdioPkgMngrResults, DirectoryManager dm) throws DetectUserFriendlyException {
        File componentsWithLocation = new File (dm.getScanOutputDirectory(), "components_with_locations.json");
        return generatePlaceHolderJsonFile(componentsWithLocation);
    }


    private static File generatePlaceHolderJsonFile(File componentsWithLocations) throws DetectUserFriendlyException {
        try {
            DetectFileUtils.writeToFile(componentsWithLocations, "{}");
        } catch (IOException ex) {
            throw new DetectUserFriendlyException("Cannot create components with locations report file", ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
        return componentsWithLocations;
    }
}
