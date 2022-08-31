package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.blackduck.api.manual.temporary.view.PolicyViolationView;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.api.manual.view.PolicyViolationLicenseView;
import com.synopsys.integration.blackduck.api.manual.view.PolicyViolationVulnerabilityView;

public class RapidScanResultAggregator {
    public RapidScanAggregateResult aggregateData(List<DeveloperScanComponentResultView> results) {
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

    private List<RapidScanComponentDetail> aggregateComponentData(List<DeveloperScanComponentResultView> results) {
        // the key is the component identifier
        List<RapidScanComponentDetail> componentDetails = new LinkedList<>();
        for (DeveloperScanComponentResultView resultView : results) {
            String componentName = resultView.getComponentName();
            RapidScanComponentDetail componentDetail = createDetail(resultView);
            componentDetails.add(componentDetail);
            RapidScanComponentGroupDetail componentGroupDetail = componentDetail.getComponentDetails();
            RapidScanComponentGroupDetail securityGroupDetail = componentDetail.getSecurityDetails();
            RapidScanComponentGroupDetail licenseGroupDetail = componentDetail.getLicenseDetails();

            // TODO this is coming in as null as the v5 endpoint changed
            // start by getting just the names from a more complex object, we can get the 
            // severity too if we wanted.
//            if (PolicyViolationView violation : resultView.getViolatingPolicies()) {
            
            
                // violating policy names is a super set of policy names so we have to remove
                // the vulnerability and license.
            
            Set<PolicyViolationView> violatingPolicies = resultView.getViolatingPolicies();
            Set<String> policyNames = violatingPolicies.stream()
                    .map(PolicyViolationView::getPolicyName)
                    .collect(Collectors.toSet());
            
                //Set<String> policyNames = new LinkedHashSet<>(resultView.getViolatingPolicyNames());
                
                Set<PolicyViolationVulnerabilityView> vulnerabilityViolations = resultView
                        .getPolicyViolationVulnerabilities();
                Set<PolicyViolationLicenseView> licenseViolations = resultView.getPolicyViolationLicenses();
                
                Set<String> vulnerabilityPolicyNames = new HashSet<>();
                for (PolicyViolationVulnerabilityView vulnerabilityViolation: vulnerabilityViolations) {
                    Set<String> partialPolicyNames = vulnerabilityViolation.getViolatingPolicies().stream()
                    .map(PolicyViolationView::getPolicyName)
                    .collect(Collectors.toSet());
                    vulnerabilityPolicyNames.addAll(partialPolicyNames);
                }
                
//                Set<String> vulnerabilityPolicyNames = vulnerabilityViolations.stream()
//                        .map(PolicyViolationVulnerabilityView::getViolatingPolicyNames).flatMap(Collection::stream)
//                        .collect(Collectors.toSet());

//                Set<String> licensePolicyNames = licenseViolations.stream()
//                        .map(PolicyViolationLicenseView::getViolatingPolicyNames).flatMap(Collection::stream)
//                        .collect(Collectors.toSet());
//                Set<String> licensePolicyNames = licenseViolations.stream()
//                        .map(PolicyViolationLicenseView::getViolatingPolicies).stream()
//                        .map(PolicyViolationView::getPolicyName)
//                        .collect(Collectors.toSet());
                
                Set<String> licensePolicyNames = new HashSet<>();
                for (PolicyViolationLicenseView licenseViolation: licenseViolations) {
                    Set<String> partialPolicyNames = licenseViolation.getViolatingPolicies().stream()
                    .map(PolicyViolationView::getPolicyName)
                    .collect(Collectors.toSet());
                    licensePolicyNames.addAll(partialPolicyNames);
                }
                
                policyNames.removeAll(vulnerabilityPolicyNames);
                policyNames.removeAll(licensePolicyNames);

                componentGroupDetail.addPolicies(policyNames);
                securityGroupDetail.addPolicies(vulnerabilityPolicyNames);
                licenseGroupDetail.addPolicies(licensePolicyNames);

                addVulnerabilityData(vulnerabilityViolations, securityGroupDetail);
                addLicenseData(licenseViolations, licenseGroupDetail);
//            }

            componentGroupDetail.addMessages(resultView::getErrorMessage, resultView::getWarningMessage);
        }
        return componentDetails;
    }

    private RapidScanComponentDetail createDetail(DeveloperScanComponentResultView view) {
        String componentName = view.getComponentName();
        String componentVersion = view.getVersionName();
        String componentIdentifier = view.getComponentIdentifier();
        RapidScanComponentGroupDetail componentGroupDetail = new RapidScanComponentGroupDetail(
                RapidScanDetailGroup.POLICY);
        RapidScanComponentGroupDetail securityGroupDetail = new RapidScanComponentGroupDetail(
                RapidScanDetailGroup.SECURITY);
        RapidScanComponentGroupDetail licenseGroupDetail = new RapidScanComponentGroupDetail(
                RapidScanDetailGroup.LICENSE);

        return new RapidScanComponentDetail(componentName, componentVersion, componentIdentifier, componentGroupDetail,
                securityGroupDetail, licenseGroupDetail);
    }

    private void addVulnerabilityData(Set<PolicyViolationVulnerabilityView> vulnerabilities,
            RapidScanComponentGroupDetail securityDetail) {
        for (PolicyViolationVulnerabilityView vulnerabilityPolicyViolation : vulnerabilities) {
            securityDetail.addMessages(vulnerabilityPolicyViolation::getErrorMessage,
                    vulnerabilityPolicyViolation::getWarningMessage);
        }
    }

    private void addLicenseData(Set<PolicyViolationLicenseView> licenses, RapidScanComponentGroupDetail licenseDetail) {
        for (PolicyViolationLicenseView licensePolicyViolation : licenses) {
            licenseDetail.addMessages(licensePolicyViolation::getErrorMessage,
                    licensePolicyViolation::getWarningMessage);
        }
    }
}
