package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;

public class RapidScanResultAggregator {
    public RapidScanAggregateResult aggregateData(List<DeveloperScansScanView> results) {
        Collection<RapidScanComponentDetail> componentDetails = aggregateComponentData(results);
        // TODO this always comes back null for some reason
//        List<RapidScanComponentDetail> sortedByComponent = componentDetails.stream()
//                .sorted(Comparator.comparing(RapidScanComponentDetail::getComponentIdentifier))
//                .collect(Collectors.toList());
        Map<RapidScanDetailGroup, RapidScanComponentGroupDetail> aggregatedDetails = new HashMap<>();
        aggregatedDetails.put(RapidScanDetailGroup.POLICY,
                new RapidScanComponentGroupDetail(RapidScanDetailGroup.POLICY));
        aggregatedDetails.put(RapidScanDetailGroup.SECURITY,
                new RapidScanComponentGroupDetail(RapidScanDetailGroup.SECURITY));
        aggregatedDetails.put(RapidScanDetailGroup.LICENSE,
                new RapidScanComponentGroupDetail(RapidScanDetailGroup.LICENSE));

        RapidScanResultSummary.Builder summaryBuilder = new RapidScanResultSummary.Builder();
        // TODO use unsorted list for now
        for (RapidScanComponentDetail detail : componentDetails) {
            summaryBuilder.addDetailData(detail);
            RapidScanDetailGroup securityGroupName = detail.getSecurityDetails().getGroup();
            RapidScanDetailGroup licenseGroupName = detail.getLicenseDetails().getGroup();
            RapidScanDetailGroup componentGroupName = detail.getComponentDetails().getGroup();

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
            
            // violating policy names is a super set of policy names so we have to remove
            // the vulnerability and license.
            List<DeveloperScansScanItemsViolatingPoliciesView> violatingPolicies = resultView.getViolatingPolicies();
            Set<String> policyNames = violatingPolicies.stream()
                    .map(DeveloperScansScanItemsViolatingPoliciesView::getPolicyName).collect(Collectors.toSet());

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

            policyNames.removeAll(vulnerabilityPolicyNames);
            policyNames.removeAll(licensePolicyNames);

            componentGroupDetail.addPolicies(policyNames);
            securityGroupDetail.addPolicies(vulnerabilityPolicyNames);
            licenseGroupDetail.addPolicies(licensePolicyNames);

            addVulnerabilityData(resultView, vulnerabilityViolations, securityGroupDetail);
            addLicenseData(resultView, licenseViolations, licenseGroupDetail);

            componentGroupDetail.addMessages(constructOverviewErrorMessage(resultView), null);
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
            securityDetail.addMessages(constructVulnerabilityErrorMessage(resultView, vulnerabilityPolicyViolation),
                    null);
        }
    }

    private void addLicenseData(DeveloperScansScanView resultView, List<DeveloperScansScanItemsPolicyViolationLicensesView> licenses, RapidScanComponentGroupDetail licenseDetail) {
        for (DeveloperScansScanItemsPolicyViolationLicensesView licensePolicyViolation : licenses) {
            licenseDetail.addMessages(constructLicenseErrorMessage(resultView, licensePolicyViolation),
                    null);
        }
    }
    
    /**
     * In v5 there are no error messages supplied by hub as there were in v4 of the
     * developer-scans endpoint. We can construct a rough error message from the other
     * fields.
     * 
     * @param resultView
     * @return
     */
    private String constructOverviewErrorMessage(DeveloperScansScanView resultView) {
        String errorMessage = "Component " + resultView.getComponentName() +
        " version " + resultView.getVersionName();  
        if (StringUtils.isNotBlank(resultView.getExternalId())) {
            errorMessage += " with ID " + resultView.getExternalId();
        }
        errorMessage += " violates policy ";
        
        List<DeveloperScansScanItemsViolatingPoliciesView> violatingPolicies = resultView.getViolatingPolicies();
        
        for (int i = 0; i < violatingPolicies.size(); i++) {
            DeveloperScansScanItemsViolatingPoliciesView violation = violatingPolicies.get(i);
                    
            errorMessage += violation.getPolicyName();
            
            if (i != violatingPolicies.size() -1) {
                errorMessage += ", ";
            }
        }
        
        return errorMessage;
    }
    
    private String constructLicenseErrorMessage(DeveloperScansScanView resultView, DeveloperScansScanItemsPolicyViolationLicensesView licensePolicyViolation) {
        String errorMessage = "Component " + resultView.getComponentName() +
        " version " + resultView.getVersionName();
        if (StringUtils.isNotBlank(resultView.getExternalId())) {
            errorMessage += " with ID " + resultView.getExternalId();
        }
        errorMessage += " violates policy ";
        
        List<DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView> violatingPolicies = licensePolicyViolation.getViolatingPolicies();
        
        for (int i = 0; i < violatingPolicies.size(); i++) {
            DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView violation = violatingPolicies.get(i);
                    
            errorMessage += violation.getPolicyName();
            
            if (i != violatingPolicies.size() -1) {
                errorMessage += ", ";
            } else {
                errorMessage += ": license " + licensePolicyViolation.getName();
            }
        }
        
        return errorMessage;
    }
    
    private String constructVulnerabilityErrorMessage(DeveloperScansScanView resultView,
            DeveloperScansScanItemsPolicyViolationVulnerabilitiesView vulnerability) {
        String errorMessage = "Component " + resultView.getComponentName() +
        " version " + resultView.getVersionName();
        if (StringUtils.isNotBlank(resultView.getExternalId())) {
            errorMessage += " with ID " + resultView.getExternalId();
        }
        errorMessage += " violates policy ";
        
        List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView> violatingPolicies = vulnerability.getViolatingPolicies();
        
        for (int i = 0; i < violatingPolicies.size(); i++) {
            DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView violation = violatingPolicies.get(i);
            
            errorMessage += violation.getPolicyName();
            
            if (i != violatingPolicies.size() -1) {
                errorMessage += ", ";
            } else {
                errorMessage += ": found vulnerability " + vulnerability.getName();
            }
        }
        
        errorMessage += " with severity " + vulnerability.getVulnSeverity();
        errorMessage += " and CVSS score " + vulnerability.getOverallScore();
        
        return errorMessage;
    }
}
