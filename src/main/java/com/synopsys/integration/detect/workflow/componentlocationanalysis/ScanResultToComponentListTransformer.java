package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;

import java.util.*;

/**
 * Transforms a list of {@link DeveloperScansScanView} to a list of {@link Component}s, which will then be used to
 * assemble the input to Component Locator.
 *
 */
public class ScanResultToComponentListTransformer {
    /**
     * Given a list of reported components from a Rapid/Stateless Detector scan, transforms each element to its
     * corresponding {@link Component} with appropriate metadata.
     * @param rapidScanFullResults
     * @return list of {@link Component}s
     */
    public List<Component> transformScanResultToComponentList(List<DeveloperScansScanView> rapidScanFullResults) {
        HashMap<String, Metadata> componentIdWithMetadata = new HashMap<>(); // TODO investigate if duplicates may make sense here?

        for (DeveloperScansScanView component : rapidScanFullResults) {
            componentIdWithMetadata.put(component.getExternalId(), populateMetadata(component));
        }

        return externalIDsToComponentList(componentIdWithMetadata);
    }

    // TODO move me to a util class
    private List<Component> externalIDsToComponentList(HashMap<String, Metadata> componentIdWithMetadata) {
        List<Component> componentList = new ArrayList<>();
        for (String gav : componentIdWithMetadata.keySet()) {
            // TODO get separator based on forge for diff pkg mngrs instead of hardcoding it here
            String[] parts = gav.split(":");
            componentList.add(new Component(parts[0], parts[1], parts[2], componentIdWithMetadata.get(gav)));
        }
        return componentList;
    }

    private Metadata populateMetadata(DeveloperScansScanView component) {
        Metadata remediationGuidance = new Metadata();
        // TODO add parts of "allVulnerabilities" section once blackduck-common-api version is bumped up (API v6 is needed)
        remediationGuidance.setComponentViolatingPolicies(component.getComponentViolatingPolicies());
        remediationGuidance.setPolicyViolationVulnerabilities(component.getPolicyViolationVulnerabilities());
        remediationGuidance.setLongTermUpgradeGuidance(component.getLongTermUpgradeGuidance());
        remediationGuidance.setShortTermUpgradeGuidance(component.getShortTermUpgradeGuidance());
        remediationGuidance.setTransitiveUpgradeGuidance(component.getTransitiveUpgradeGuidance());
        return remediationGuidance;
    }
}
