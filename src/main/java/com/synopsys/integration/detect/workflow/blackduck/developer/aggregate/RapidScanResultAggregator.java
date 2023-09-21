package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsComponentViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsTransitiveUpgradeGuidanceLongTermUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsTransitiveUpgradeGuidanceShortTermUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsTransitiveUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsView;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;

public class RapidScanResultAggregator {
    
    private final Map<String, Set<String>> directToTransitiveChildren = new HashMap<>();
    private final Map<String, String[]> directUpgradeGuidanceVersions = new HashMap<>();
    
    public RapidScanAggregateResult aggregateData(List<DeveloperScansScanView> results) {
        Collection<RapidScanComponentDetail> componentDetails = aggregateComponentData(results);
        List<RapidScanComponentDetail> sortedByComponent = componentDetails.stream()
                .sorted(Comparator.comparing(RapidScanComponentDetail::getComponentIdentifier))
                .collect(Collectors.toList());
        Map<RapidScanDetailGroup, RapidScanComponentGroupDetail> aggregatedDetails = new HashMap<>();
        aggregatedDetails.put(RapidScanDetailGroup.POLICY, new RapidScanComponentGroupDetail(RapidScanDetailGroup.POLICY));
        aggregatedDetails.put(RapidScanDetailGroup.SECURITY, new RapidScanComponentGroupDetail(RapidScanDetailGroup.SECURITY));
        aggregatedDetails.put(RapidScanDetailGroup.LICENSE, new RapidScanComponentGroupDetail(RapidScanDetailGroup.LICENSE));

        RapidScanResultSummary.Builder summaryBuilder = new RapidScanResultSummary.Builder();

        for (RapidScanComponentDetail detail : sortedByComponent) {
            summaryBuilder.addDetailData(detail);
            
            RapidScanComponentGroupDetail aggregatedSecurityDetail = aggregatedDetails.get(detail.getSecurityDetails().getGroup());
            RapidScanComponentGroupDetail aggregatedLicenseDetail = aggregatedDetails.get(detail.getLicenseDetails().getGroup());
            RapidScanComponentGroupDetail aggregatedComponentDetail = aggregatedDetails.get(detail.getComponentDetails().getGroup());

            aggregatedComponentDetail.addErrors(detail.getComponentDetails().getErrorMessages());
            aggregatedComponentDetail.addWarnings(detail.getComponentDetails().getWarningMessages());
            aggregatedSecurityDetail.addErrors(detail.getSecurityDetails().getErrorMessages());
            aggregatedSecurityDetail.addWarnings(detail.getSecurityDetails().getWarningMessages());
            aggregatedLicenseDetail.addErrors(detail.getLicenseDetails().getErrorMessages());
            aggregatedLicenseDetail.addWarnings(detail.getLicenseDetails().getWarningMessages());
        }

        List<String> transitiveGuidance = this.transitiveGuidanceDetails();
        summaryBuilder.addTransitiveGuidances(new LinkedHashSet<>(transitiveGuidance));
        return new RapidScanAggregateResult(summaryBuilder.build(), aggregatedDetails.get(RapidScanDetailGroup.POLICY),
                aggregatedDetails.get(RapidScanDetailGroup.SECURITY),
                aggregatedDetails.get(RapidScanDetailGroup.LICENSE),
                transitiveGuidance);
    }

    private List<RapidScanComponentDetail> aggregateComponentData(List<DeveloperScansScanView> results) {
        // the key is the component identifier
        List<RapidScanComponentDetail> componentDetails = new LinkedList<>();
        
        for (DeveloperScansScanView resultView : results) {
            processViewItems(componentDetails, resultView);
        }
        
        return componentDetails;
    }

    /**
     * @param componentDetails
     * @param resultView
     */
    private void processViewItems(List<RapidScanComponentDetail> componentDetails, DeveloperScansScanView highLevelView) {
        
        for (DeveloperScansScanItemsView resultItem : highLevelView.getItems()) {
        
        compileTransitiveGuidance(resultItem);

        RapidScanComponentDetail componentDetail = createDetail(resultItem);
        componentDetails.add(componentDetail);
        RapidScanComponentGroupDetail componentGroupDetail = componentDetail.getComponentDetails();
        RapidScanComponentGroupDetail securityGroupDetail = componentDetail.getSecurityDetails();
        RapidScanComponentGroupDetail licenseGroupDetail = componentDetail.getLicenseDetails();
              
        List<DeveloperScansScanItemsComponentViolatingPoliciesView> componentViolations = 
                resultItem.getComponentViolatingPolicies();
        List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesView> vulnerabilityViolations = resultItem
                .getPolicyViolationVulnerabilities();
        List<DeveloperScansScanItemsPolicyViolationLicensesView> licenseViolations = resultItem
                .getPolicyViolationLicenses();

        Set<String> vulnerabilityPolicyNames = vulnerabilityViolations.stream()
                .map(DeveloperScansScanItemsPolicyViolationVulnerabilitiesView::getViolatingPolicies)
                .flatMap(Collection::stream)
                .map(DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView::getPolicyName)
                .collect(Collectors.toSet());

        Set<String> licensePolicyNames = licenseViolations.stream()
                .map(DeveloperScansScanItemsPolicyViolationLicensesView::getViolatingPolicies)
                .flatMap(Collection::stream)
                .map(DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView::getPolicyName)
                .collect(Collectors.toSet());
        
        Set<String> componentPolicyNames = componentViolations.stream()
                .map(DeveloperScansScanItemsComponentViolatingPoliciesView::getPolicyName)
                .collect(Collectors.toSet());

        componentGroupDetail.addPolicies(componentPolicyNames);
        securityGroupDetail.addPolicies(vulnerabilityPolicyNames);
        licenseGroupDetail.addPolicies(licensePolicyNames);

        addComponentData(resultItem, componentViolations, componentGroupDetail);
        addVulnerabilityData(resultItem, vulnerabilityViolations, securityGroupDetail);
        addLicenseData(resultItem, licenseViolations, licenseGroupDetail);
        
        }
    }

    /**
     * This is handled here because we have to use the data in a "look back" type of scenario.  
     * We are getting lists of components and they in turn have transitive guidance if they are 
     * transitive components.  We must base this list on the direct dependency component that come
     * in the trans. guidance section, effectively reversing the order.  By handling that here, we
     * can get a proper lookup of direct comp. -> child component(s).  Once this is completed,
     * we can put together the string messages needed.
     * 
     * @param resultItem 
     * @param results
     */
    private void compileTransitiveGuidance(DeveloperScansScanItemsView resultItem) {
        List<DeveloperScansScanItemsTransitiveUpgradeGuidanceView> transitiveGuidance = resultItem.getTransitiveUpgradeGuidance();
        if (transitiveGuidance != null && !transitiveGuidance.isEmpty()) {
            String childExternalId = resultItem.getExternalId();
            for (DeveloperScansScanItemsTransitiveUpgradeGuidanceView guidance : transitiveGuidance) {
                String parentId = guidance.getExternalId();
                String[] versions = getversionsFromUpgradeGuidance(guidance);
                this.directUpgradeGuidanceVersions.put(parentId, versions);
                addChildrenToParent(parentId, childExternalId);
            }
        }
    }
    
    private void addChildrenToParent(String parentId, String childExternalId) {
        // accumulate transitive information for direct dep. and list of child deps.
        if (directToTransitiveChildren.containsKey(parentId)) {
            directToTransitiveChildren.get(parentId).add(childExternalId);
        } else {
            Set<String> children = new HashSet<>();
            children.add(childExternalId);
            directToTransitiveChildren.put(parentId, children);
        }
    }

    private String[] getversionsFromUpgradeGuidance(DeveloperScansScanItemsTransitiveUpgradeGuidanceView guidance) {
        DeveloperScansScanItemsTransitiveUpgradeGuidanceShortTermUpgradeGuidanceView stg = guidance.getShortTermUpgradeGuidance();
        DeveloperScansScanItemsTransitiveUpgradeGuidanceLongTermUpgradeGuidanceView ltg = guidance.getLongTermUpgradeGuidance();
        String shortTermVersion = stg != null ? stg.getVersionName() : "";
        String longTermVersion = ltg != null ? ltg.getVersionName() : "";
        /* stg or ltg can be null.  If one or the other is null, then we'll 
        overwrite the one of the versions with the non-null version
        and suggest that in the guidance (as if stg.version = ltg.version) */
        if ( stg == null && ltg != null) {
            shortTermVersion = longTermVersion;
        } else if (stg != null && ltg == null) {
            longTermVersion = shortTermVersion;
        }
        return new String[] { shortTermVersion, longTermVersion };
    }  

    private RapidScanComponentDetail createDetail(DeveloperScansScanItemsView resultItem) {
        String componentName = resultItem.getComponentName();
        String componentVersion = resultItem.getVersionName();
        String componentIdentifier = "";
        
        if (StringUtils.isNotBlank(resultItem.getComponentIdentifier())) {
            componentIdentifier = resultItem.getComponentIdentifier();
        } else if (StringUtils.isNotBlank(resultItem.getExternalId())) {
            componentIdentifier = resultItem.getExternalId();
        }
        
        RapidScanComponentGroupDetail componentGroupDetail = new RapidScanComponentGroupDetail(RapidScanDetailGroup.POLICY);
        RapidScanComponentGroupDetail securityGroupDetail = new RapidScanComponentGroupDetail(RapidScanDetailGroup.SECURITY);
        RapidScanComponentGroupDetail licenseGroupDetail = new RapidScanComponentGroupDetail(RapidScanDetailGroup.LICENSE);

        return new RapidScanComponentDetail(componentName, componentVersion, componentIdentifier, componentGroupDetail,
                securityGroupDetail, licenseGroupDetail);
    }

    private void addVulnerabilityData(DeveloperScansScanItemsView resultItem, List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesView> vulnerabilities, RapidScanComponentGroupDetail securityDetail) {
        for (DeveloperScansScanItemsPolicyViolationVulnerabilitiesView vulnerabilityPolicyViolation : vulnerabilities) {
            securityDetail.addVulnerabilityMessages(resultItem, vulnerabilityPolicyViolation);
        }
    }

    private void addLicenseData(DeveloperScansScanItemsView resultItem, List<DeveloperScansScanItemsPolicyViolationLicensesView> licenseViolations, RapidScanComponentGroupDetail licenseDetail) {
        for (DeveloperScansScanItemsPolicyViolationLicensesView licensePolicyViolation : licenseViolations) {
            licenseDetail.addLicenseMessages(resultItem, licensePolicyViolation);
        }
    }
    
    private void addComponentData(DeveloperScansScanItemsView resultItem, List<DeveloperScansScanItemsComponentViolatingPoliciesView> componentViolations, RapidScanComponentGroupDetail componentGroupDetail) {
        for (DeveloperScansScanItemsComponentViolatingPoliciesView componentPolicyViolation: componentViolations) {
            componentGroupDetail.addComponentMessages(resultItem, componentPolicyViolation);
        }
    }

    /**
     * Special case handler for formatting the transitive guidance suggestions and handing
     * them off to be reported...
     * 
     * @return  String list of transitive guidance suggestions.
     */
    private List<String> transitiveGuidanceDetails() {
        ArrayList<String> guidances = new ArrayList<>();

        Set<String> componentKeys = this.directToTransitiveChildren.keySet();
        for (String key : componentKeys) {
            String plural = "s";
            String externalId = key;

            Set<String> children = this.directToTransitiveChildren.get(externalId);
            String childComponents = StringUtils.join(children, ", ");
            if (children.size() == 1) {
                plural = "";
            }
            
            String versionsToUse;
            String[] versions = this.directUpgradeGuidanceVersions.get(externalId);
            if (versions[1] != null && versions[0] != null   && versions[1].equals(versions[0])) {
                versionsToUse = "version ".concat(versions[1]);
            } else {
                versionsToUse = "versions (Short Term) ".concat(versions[0]).concat(", ").concat("(Long Term) ").concat(versions[1]);
            }
            
            String guidance = String.format("Upgrade component %s to %s in order to upgrade transitive component%s %s", externalId, versionsToUse, plural, childComponents);
            guidances.add(guidance);
        }
        return guidances;
    }
}
