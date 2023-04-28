package com.synopsys.integration.detect.workflow.report.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

import java.io.File;
import java.util.List;

public class ComponentLocationAnalysis {

    public static void generateLocationFileForNonPersistentDetectorScan(List<DeveloperScansScanView> rapidFullResults, DirectoryManager dm) {
        File componentsWithLocation = new File (dm.getScanOutputDirectory(), "components-with-location.json");
    }

    public static void generateLocationFileForOfflineScan(BdioResult bdioPkgMngrResults, DirectoryManager dm) {
        File componentsWithLocation = new File (dm.getScanOutputDirectory(), "components-with-location.json");
    }
}
