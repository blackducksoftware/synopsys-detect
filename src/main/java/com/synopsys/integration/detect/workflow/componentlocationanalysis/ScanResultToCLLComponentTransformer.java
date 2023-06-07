package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import java.util.List;

/**
 * Transforms a list of {@link DeveloperScansScanView} to {@link CLLComponent}
 */
public class ScanResultToCLLComponentTransformer {
    public ComponentLocatorLibraryInput transformToComponentLocatorInput(List<DeveloperScansScanView> rapidScanFullResults) {
        return new ComponentLocatorLibraryInput(null, null, null);
    }
}
