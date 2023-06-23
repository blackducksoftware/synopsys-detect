package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import java.util.List;

/**
 * Transforms a list of {@link DeveloperScansScanView} to a list of {@link Component}s, which will then be used to
 * assemble the input to Component Locator.
 *
 * Will be fully implemented in a subsequent pull request to the Fix PR feature branch.
 */
public class ScanResultToComponentListTransformer {
    public ComponentLocatorInput transformScanResultToComponentList(List<DeveloperScansScanView> rapidScanFullResults) {
        return new ComponentLocatorInput(null, null, null);
    }
}
