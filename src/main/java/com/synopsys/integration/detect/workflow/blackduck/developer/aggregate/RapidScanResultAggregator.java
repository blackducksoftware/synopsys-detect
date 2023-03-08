package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
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
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsAllVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsComponentViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationLicensesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsTransitiveUpgradeGuidanceLongTermUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsTransitiveUpgradeGuidanceShortTermUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsTransitiveUpgradeGuidanceView;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.api.generated.view.ScanFullResultView;
import com.synopsys.integration.log.IntLogger;

public class RapidScanResultAggregator {
    public RapidScanAggregateResult aggregateData(List<ScanFullResultView> results) {
        Collection<RapidScanComponentDetail> componentDetails = aggregateComponentData(results);
        List<RapidScanComponentDetail> sortedByComponent = componentDetails.stream()
                .sorted(Comparator.comparing(RapidScanComponentDetail::getComponentIdentifier))
                .collect(Collectors.toList());
        Map<RapidScanDetailGroup, RapidScanComponentGroupDetail> aggregatedDetails = new HashMap<>();
        aggregatedDetails.put(RapidScanDetailGroup.POLICY,
                new RapidScanComponentGroupDetail(RapidScanDetailGroup.POLICY));
        aggregatedDetails.put(RapidScanDetailGroup.SECURITY,
                new RapidScanComponentGroupDetail(RapidScanDetailGroup.SECURITY));
        aggregatedDetails.put(RapidScanDetailGroup.LICENSE,
                new RapidScanComponentGroupDetail(RapidScanDetailGroup.LICENSE));

        RapidScanResultSummary.Builder summaryBuilder = new RapidScanResultSummary.Builder();

        for (RapidScanComponentDetail detail : sortedByComponent) {
            summaryBuilder.addDetailData(detail);

            RapidScanComponentGroupDetail aggregatedSecurityDetail = aggregatedDetails
                    .get(detail.getSecurityDetails().getGroup());
            RapidScanComponentGroupDetail aggregatedLicenseDetail = aggregatedDetails
                    .get(detail.getLicenseDetails().getGroup());
            RapidScanComponentGroupDetail aggregatedComponentDetail = aggregatedDetails
                    .get(detail.getComponentDetails().getGroup());

            aggregatedComponentDetail.addErrors(detail.getComponentDetails().getErrorMessages());
            aggregatedComponentDetail.addWarnings(detail.getComponentDetails().getWarningMessages());
            aggregatedSecurityDetail.addErrors(detail.getSecurityDetails().getErrorMessages());
            aggregatedSecurityDetail.addWarnings(detail.getSecurityDetails().getWarningMessages());
            aggregatedLicenseDetail.addErrors(detail.getLicenseDetails().getErrorMessages());
            aggregatedLicenseDetail.addWarnings(detail.getLicenseDetails().getWarningMessages());
        }

        List<String> transitiveGuidance = this.transitiveGuidanceDetails();
        summaryBuilder.addTransitiveGuidances(new LinkedHashSet<String>(transitiveGuidance));
        return new RapidScanAggregateResult(summaryBuilder.build(), aggregatedDetails.get(RapidScanDetailGroup.POLICY),
                aggregatedDetails.get(RapidScanDetailGroup.SECURITY),
                aggregatedDetails.get(RapidScanDetailGroup.LICENSE),
                transitiveGuidance);
    }

    private List<RapidScanComponentDetail> aggregateComponentData(List<ScanFullResultView> results) {
        // the key is the component identifier
        List<RapidScanComponentDetail> componentDetails = new LinkedList<>();

        for (ScanFullResultView resultView : results) {
            this.compileTransitiveGuidance(resultView, results);

            String componentName = resultView.getComponentName();
            RapidScanComponentDetail componentDetail = createDetail(resultView);
            componentDetails.add(componentDetail);
            RapidScanComponentGroupDetail componentGroupDetail = componentDetail.getComponentDetails();
            RapidScanComponentGroupDetail securityGroupDetail = componentDetail.getSecurityDetails();
            RapidScanComponentGroupDetail licenseGroupDetail = componentDetail.getLicenseDetails();
                  
            List<ScanFullResultItemsComponentViolatingPoliciesView> componentViolations = 
                    resultView.getComponentViolatingPolicies();
            List<ScanFullResultItemsPolicyViolationVulnerabilitiesView> vulnerabilityViolations = resultView
                    .getPolicyViolationVulnerabilities();
            List<ScanFullResultItemsPolicyViolationLicensesView> licenseViolations = resultView
                    .getPolicyViolationLicenses();

            Set<String> vulnerabilityPolicyNames = vulnerabilityViolations.stream()
                    .map(ScanFullResultItemsPolicyViolationVulnerabilitiesView::getViolatingPolicies)
                    .flatMap(Collection::stream)
                    .map(ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView::getPolicyName)
                    .collect(Collectors.toSet());

            Set<String> licensePolicyNames = licenseViolations.stream()
                    .map(ScanFullResultItemsPolicyViolationLicensesView::getViolatingPolicies)
                    .flatMap(Collection::stream)
                    .map(ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView::getPolicyName)
                    .collect(Collectors.toSet());
            
            Set<String> componentPolicyNames = componentViolations.stream()
                    .map(ScanFullResultItemsComponentViolatingPoliciesView::getPolicyName)
                    .collect(Collectors.toSet());

            componentGroupDetail.addPolicies(componentPolicyNames);
            securityGroupDetail.addPolicies(vulnerabilityPolicyNames);
            licenseGroupDetail.addPolicies(licensePolicyNames);

            addComponentData(resultView, componentViolations, componentGroupDetail);
            addVulnerabilityData(resultView, vulnerabilityViolations, securityGroupDetail);
            addLicenseData(resultView, licenseViolations, licenseGroupDetail);
        }
        
        return componentDetails;
    }

    private HashMap<String, List<String>> directToTransitiveChildren = new HashMap<>();
    private HashMap<String, String[]> directUpgradeGuidanceVersions = new HashMap<>();
    /**
     * This is handled here because we have to use the data in a "look back" type of scenario.  
     * We are getting lists of components and they in turn have transitive guidance if they are 
     * transitive components.  We must base this list on the direct dependency component that come
     * in the trans. guidance section, effectively reversing the order.  By handling that here, we
     * can get a proper lookup of direct comp. -> child component(s).  Once this is completed,
     * we can put together the string messages needed.
     * 
     * @param resultView 
     * @param results
     */
    private void compileTransitiveGuidance(ScanFullResultView resultView, List<ScanFullResultView> results) {
        List<ScanFullResultItemsTransitiveUpgradeGuidanceView> transitiveGuidance = resultView.getTransitiveUpgradeGuidance();
        if (transitiveGuidance == null || transitiveGuidance.size() <= 0) {
            return;
        }

        String childExternalId = resultView.getExternalId();
        if (transitiveGuidance.size() > 0  && resultView.getAllVulnerabilities().size() > 0) {// we'll only count things that HAVE vulnerabilities

            for (ScanFullResultItemsTransitiveUpgradeGuidanceView guidance : transitiveGuidance) {
                String parentId = guidance.getExternalId();
                ScanFullResultItemsTransitiveUpgradeGuidanceShortTermUpgradeGuidanceView stg = guidance
                        .getShortTermUpgradeGuidance();
                ScanFullResultItemsTransitiveUpgradeGuidanceLongTermUpgradeGuidanceView ltg = guidance
                        .getLongTermUpgradeGuidance();
                String shortTermVersion = stg.getVersionName();
                String longTermVersion = ltg.getVersionName();

                String[] versions = new String[] { shortTermVersion, longTermVersion };
                this.directUpgradeGuidanceVersions.put(parentId, versions);
                // accumulate transitive information for direct dep. and list of child deps.
                if (directToTransitiveChildren.containsKey(parentId)) {
                    List<String> children = directToTransitiveChildren.get(parentId);
                    if (null != children && !children.contains(childExternalId)) {
                        children.add(childExternalId);
                    }
                } else {
                    List<String> children = new ArrayList<>();
                    children.add(childExternalId);
                    directToTransitiveChildren.put(parentId, children);
                }
            }
        }
        return;
    }

       

    private RapidScanComponentDetail createDetail(ScanFullResultView resultView) {
        String componentName = resultView.getComponentName();
        String componentVersion = resultView.getVersionName();
        
        String componentIdentifier = "";
        if (StringUtils.isNotBlank(resultView.getComponentIdentifier())) {
            componentIdentifier = resultView.getComponentIdentifier();
        } else if (StringUtils.isNotBlank(resultView.getExternalId())) {
            componentIdentifier = resultView.getExternalId();
        }
        RapidScanComponentGroupDetail componentGroupDetail = new RapidScanComponentGroupDetail(
                RapidScanDetailGroup.POLICY);
        RapidScanComponentGroupDetail securityGroupDetail = new RapidScanComponentGroupDetail(
                RapidScanDetailGroup.SECURITY);
        RapidScanComponentGroupDetail licenseGroupDetail = new RapidScanComponentGroupDetail(
                RapidScanDetailGroup.LICENSE);

        return new RapidScanComponentDetail(componentName, componentVersion, componentIdentifier, componentGroupDetail,
                securityGroupDetail, licenseGroupDetail);
    }

    private void addVulnerabilityData(ScanFullResultView resultView, List<ScanFullResultItemsPolicyViolationVulnerabilitiesView> vulnerabilities,
            RapidScanComponentGroupDetail securityDetail) {
        for (ScanFullResultItemsPolicyViolationVulnerabilitiesView vulnerabilityPolicyViolation : vulnerabilities) {
            securityDetail.addVulnerabilityMessages(resultView, vulnerabilityPolicyViolation);
        }
    }

    private void addLicenseData(ScanFullResultView resultView, List<ScanFullResultItemsPolicyViolationLicensesView> licenseViolations, RapidScanComponentGroupDetail licenseDetail) {
        for (ScanFullResultItemsPolicyViolationLicensesView licensePolicyViolation : licenseViolations) {
            licenseDetail.addLicenseMessages(resultView, licensePolicyViolation);
        }
    }
    
    private void addComponentData(ScanFullResultView resultView, List<ScanFullResultItemsComponentViolatingPoliciesView> componentViolations, RapidScanComponentGroupDetail componentGroupDetail) {
        for (ScanFullResultItemsComponentViolatingPoliciesView componentPolicyViolation: componentViolations) {
            componentGroupDetail.addComponentMessages(resultView, componentPolicyViolation);
        }
        
    }

    /**
     * Special case handler for formatting the transitive guidance suggestions and handing
     * them off to be reported...
     * 
     * @return  String list of transitive guidance suggestions.
     */
    private List<String> transitiveGuidanceDetails() {
        String groupName = RapidScanDetailGroup.TRANSITIVE.getDisplayName();
        String componentMsgString = "component";
        ArrayList<String> guidances = new ArrayList<>();

        Set<String> componentKeys = this.directToTransitiveChildren.keySet();
        for (String key : componentKeys) {
            String plural = "s";

            String externalId = key;

            List<String> children = this.directToTransitiveChildren.get(externalId);
            String childComponents = StringUtils.join(children, ", ");
            if (children.size() == 1) {
                plural = "";
            }
            
            String versionsToUse = "";
            String[] versions = this.directUpgradeGuidanceVersions.get(externalId);
            if (versions[1] != null && versions[0] != null   && versions[1].equals(versions[0])) {
                versionsToUse = "version " + versions[1];
            } else {
                versionsToUse = "versions (Short Term) " + versions[0] +", " + "(Long Term) " + versions[1];
            }
            
            String guidance = String.format("Upgrade component %s to %s in order to upgrade transitive component%s %s", externalId, versionsToUse, plural, childComponents);
            guidances.add(guidance);
        }
        return guidances;
    }
}
