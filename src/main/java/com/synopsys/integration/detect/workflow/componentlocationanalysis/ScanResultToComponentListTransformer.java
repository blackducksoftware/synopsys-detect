package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.componentlocator.beans.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms a list of {@link DeveloperScansScanView} to a list of {@link Component}s, which will then be used to
 * assemble the input to Component Locator.
 *
 */
public class ScanResultToComponentListTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Given a list of reported components from a Rapid/Stateless Detector scan, transforms each element to its
     * corresponding {@link Component} with appropriate metadata.
     * @param rapidScanFullResults
     * @return list of {@link Component}s
     */
    public Set<Component> transformScanResultToComponentList(List<DeveloperScansScanView> rapidScanFullResults) {
        HashMap<String, ScanMetadata> componentIdWithMetadata = new HashMap<>();
        Set<String> componentNamesWithNullIds = new HashSet<>(rapidScanFullResults.size());
        for (DeveloperScansScanView component : rapidScanFullResults) {
            if (component.getExternalId() == null) {
                componentNamesWithNullIds.add(component.getComponentName());
            } else {
                componentIdWithMetadata.put(component.getExternalId(), populateMetadata(component));
            }
        }
        for (String componentNameToWarnAbout : componentNamesWithNullIds) {
            logger.warn("Component '{}' is skipped due to missing external ID in the scan result json.", componentNameToWarnAbout);
        }

        return convertExternalIDsToComponentList(componentIdWithMetadata);
    }

    private Set<Component> convertExternalIDsToComponentList(HashMap<String, ScanMetadata> componentIdWithMetadata) {
        Set<Component> componentSet = new HashSet<>();
        try {
            for (String componentIdString : componentIdWithMetadata.keySet()) {
                String[] parts;
                if ((parts = componentIdString.split(":")).length == 3) {
                    // For Maven and Gradle, the componentId is of the form "g:a:v"
                    componentSet.add(new Component(parts[0], parts[1], parts[2], getJsonObjectFromScanMetadata(componentIdWithMetadata.get(componentIdString))));
                } else if ((parts = componentIdString.split("/")).length == 2) {
                    // For NPM and NuGet, the componentId looks is of the form "a/v"
                    componentSet.add(new Component(null, parts[0], parts[1], getJsonObjectFromScanMetadata(componentIdWithMetadata.get(componentIdString))));
                }
            }
        } catch (Exception e) {
            logger.debug("There was a problem processing component IDs from scan results during Component Location Analysis: {}", e);
        }
        return componentSet;
    }

    private JsonObject getJsonObjectFromScanMetadata(ScanMetadata scanMeta) {
        Gson gson = new GsonBuilder().create();
        JsonElement element = gson.toJsonTree(scanMeta);
        JsonObject object = element.getAsJsonObject();
        return object;
    }

    private ScanMetadata populateMetadata(DeveloperScansScanView component) {
        ScanMetadata remediationGuidance = new ScanMetadata();
        remediationGuidance.setComponentViolatingPolicies(component.getComponentViolatingPolicies());
        remediationGuidance.setPolicyViolationVulnerabilities(component.getPolicyViolationVulnerabilities());
        remediationGuidance.setLongTermUpgradeGuidance(component.getLongTermUpgradeGuidance());
        remediationGuidance.setShortTermUpgradeGuidance(component.getShortTermUpgradeGuidance());
        remediationGuidance.setTransitiveUpgradeGuidance(component.getTransitiveUpgradeGuidance());
        return remediationGuidance;
    }
}
