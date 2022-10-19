package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
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
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;

public class RapidScanResultAggregator {
    public RapidScanAggregateResult aggregateData(List<DeveloperScansScanView> results) {
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

        return new RapidScanAggregateResult(summaryBuilder.build(), aggregatedDetails.get(RapidScanDetailGroup.POLICY),
                aggregatedDetails.get(RapidScanDetailGroup.SECURITY),
                aggregatedDetails.get(RapidScanDetailGroup.LICENSE));
    }

    private List<RapidScanComponentDetail> aggregateComponentData(List<DeveloperScansScanView> results) {
        // the key is the component identifier
        List<RapidScanComponentDetail> componentDetails = new LinkedList<>();
        for (DeveloperScansScanView resultView : results) {
            String componentName = resultView.getComponentName();
            RapidScanComponentDetail componentDetail = createDetail(resultView);
            componentDetails.add(componentDetail);
            RapidScanComponentGroupDetail componentGroupDetail = componentDetail.getComponentDetails();
            RapidScanComponentGroupDetail securityGroupDetail = componentDetail.getSecurityDetails();
            RapidScanComponentGroupDetail licenseGroupDetail = componentDetail.getLicenseDetails();
                  
            List<DeveloperScansScanItemsComponentViolatingPoliciesView> componentViolations = 
                    resultView.getComponentViolatingPolicies();
            List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesView> vulnerabilityViolations = resultView
                    .getPolicyViolationVulnerabilities();
            List<DeveloperScansScanItemsPolicyViolationLicensesView> licenseViolations = resultView
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

            addComponentData(resultView, componentViolations, componentGroupDetail);
            addVulnerabilityData(resultView, vulnerabilityViolations, securityGroupDetail);
            addLicenseData(resultView, licenseViolations, licenseGroupDetail);
        }
        return componentDetails;
    }

    private RapidScanComponentDetail createDetail(DeveloperScansScanView view) {
        String componentName = view.getComponentName();
        String componentVersion = view.getVersionName();
        
        String componentIdentifier = "";
        if (StringUtils.isNotBlank(view.getComponentIdentifier())) {
            componentIdentifier = view.getComponentIdentifier();
        } else if (StringUtils.isNotBlank(view.getExternalId())) {
            componentIdentifier = view.getExternalId();
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

    private void addVulnerabilityData(DeveloperScansScanView resultView, List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesView> vulnerabilities,
            RapidScanComponentGroupDetail securityDetail) {
        for (DeveloperScansScanItemsPolicyViolationVulnerabilitiesView vulnerabilityPolicyViolation : vulnerabilities) {
            securityDetail.addVulnerabilityMessages(resultView, vulnerabilityPolicyViolation);
        }
    }

    private void addLicenseData(DeveloperScansScanView resultView, List<DeveloperScansScanItemsPolicyViolationLicensesView> licenses, RapidScanComponentGroupDetail licenseDetail) {
        for (DeveloperScansScanItemsPolicyViolationLicensesView licensePolicyViolation : licenses) {
            licenseDetail.addLicenseMessages(resultView, licensePolicyViolation);
        }
    }
    
    private void addComponentData(DeveloperScansScanView resultView, List<DeveloperScansScanItemsComponentViolatingPoliciesView> componentViolations, RapidScanComponentGroupDetail componentGroupDetail) {
        for (DeveloperScansScanItemsComponentViolatingPoliciesView componentPolicyViolation: componentViolations) {
            componentGroupDetail.addComponentMessages(resultView, componentPolicyViolation);
        }
        
    }
}
