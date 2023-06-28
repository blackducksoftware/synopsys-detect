package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsLongTermUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsShortTermUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsTransitiveUpgradeGuidanceView;

import java.util.List;

/**
 * Represents the way Detect chooses to populate the optional metadata field of Component Locator's input file...
 */
public class Metadata {
    private List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesView> policyViolationVulnerabilities;
    private DeveloperScansScanItemsShortTermUpgradeGuidanceView shortTermUpgradeGuidance;
    private DeveloperScansScanItemsLongTermUpgradeGuidanceView longTermUpgradeGuidance;
    private List<DeveloperScansScanItemsTransitiveUpgradeGuidanceView> transitiveUpgradeGuidance;

    public void setPolicyViolationVulnerabilities(List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesView> policyViolationVulnerabilities) {
        this.policyViolationVulnerabilities = policyViolationVulnerabilities;
    }

    public void setShortTermUpgradeGuidance(DeveloperScansScanItemsShortTermUpgradeGuidanceView shortTermUpgradeGuidance) {
        this.shortTermUpgradeGuidance = shortTermUpgradeGuidance;
    }

    public void setLongTermUpgradeGuidance(DeveloperScansScanItemsLongTermUpgradeGuidanceView longTermUpgradeGuidance) {
        this.longTermUpgradeGuidance = longTermUpgradeGuidance;
    }

    public void setTransitiveUpgradeGuidance(List<DeveloperScansScanItemsTransitiveUpgradeGuidanceView> transitiveUpgradeGuidance) {
        this.transitiveUpgradeGuidance = transitiveUpgradeGuidance;
    }
}
