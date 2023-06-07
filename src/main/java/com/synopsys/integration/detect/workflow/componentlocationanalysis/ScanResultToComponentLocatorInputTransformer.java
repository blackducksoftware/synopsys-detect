package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import java.util.List;

/**
 * Transforms a list of {@link DeveloperScansScanView} to {@link ComponentLocatorLibInput}
 */
public class ScanResultToComponentLocatorInputTransformer {
    public ComponentLocatorLibInput transformToComponentLocatorInput(List<DeveloperScansScanView> rapidScanFullResults) {
        return new ComponentLocatorLibInput(null, null, null);
    }
}
