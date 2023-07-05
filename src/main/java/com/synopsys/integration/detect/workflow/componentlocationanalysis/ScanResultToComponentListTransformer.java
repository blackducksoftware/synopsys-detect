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
    public List<Component> transformScanResultToComponentList(List<DeveloperScansScanView> rapidScanFullResults) {
        Set<String> externalIds = new HashSet<>(); // TOME duplicates may make sense here?
        HashMap<String, Metadata> componentIdWithMetadata = new HashMap<>();

        for (DeveloperScansScanView component : rapidScanFullResults) {
            componentIdWithMetadata.put(component.getExternalId(), populateMetadata(component));
        }

        return externalIDsToComponentList(componentIdWithMetadata);
    }

    // TODO move me to a util class
    private List<Component> externalIDsToComponentList(HashMap<String, Metadata> componentIdWithMetadata) {
        List<Component> componentList = new ArrayList<>();
        for (String gav : componentIdWithMetadata.keySet()) {
            String[] parts = gav.split(":"); // TODO get separator based on forge for diff pkg mngrs
            componentList.add(new Component(parts[0], parts[1], parts[2], componentIdWithMetadata.get(gav)));
        }
        return componentList;
    }

    private Metadata populateMetadata(DeveloperScansScanView component) {
        Metadata remediationGuidance = new Metadata();
        remediationGuidance.setPolicyViolationVulnerabilities(component.getPolicyViolationVulnerabilities());
        remediationGuidance.setLongTermUpgradeGuidance(component.getLongTermUpgradeGuidance());
        remediationGuidance.setShortTermUpgradeGuidance(component.getShortTermUpgradeGuidance());
        remediationGuidance.setTransitiveUpgradeGuidance(component.getTransitiveUpgradeGuidance());
        return remediationGuidance;
    }
}
