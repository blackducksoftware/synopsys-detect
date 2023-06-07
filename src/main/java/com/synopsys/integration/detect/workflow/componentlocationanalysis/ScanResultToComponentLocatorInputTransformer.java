package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsLongTermUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsShortTermUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import java.util.List;

/**
 * Transforms a list of {@link DeveloperScansScanView} to {@link ComponentLocatorLibInput}
 */
public class ScanResultToComponentLocatorInputTransformer {

    public void ComponentLocatorInput transformToComponentLocatorInput(List<DeveloperScansScanView> rapidScanFullResults) {
        // for each vulnerable component,  look at its external ID, upgrade guidance and transitive dependencies if any.
        // add each component to ComponentList in the input json
    }

    private CLLComponent transformToCLLComponent(ExternalId externalId,
                                                 List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesView> policyViolationVulnerabilities,
                                                 DeveloperScansScanItemsShortTermUpgradeGuidanceView shortTermUpgradeGuidance,
                                                 DeveloperScansScanItemsLongTermUpgradeGuidanceView longTermUpgradeGuidance) {
        CLLMetadata componentSpecificMeta = new CLLMetadata();
        return CLLComponent(externalId, componentSpecificMeta);
    }
}
