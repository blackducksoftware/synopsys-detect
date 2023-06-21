package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import java.util.List;

/**
 * Transforms a list of {@link DeveloperScansScanView} to {@link CLLComponent}
 *
 * Will be fully implemented in a subsequent pull request to the Fix PR feature branch.
 */
public class ScanResultToCLLComponentTransformer {
    public ComponentLocatorLibraryInput transformToComponentLocatorInput(List<DeveloperScansScanView> rapidScanFullResults) {
        return new ComponentLocatorLibraryInput(null, null, null);
    }
}
