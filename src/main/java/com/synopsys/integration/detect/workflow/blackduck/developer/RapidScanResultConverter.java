/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.api.manual.view.PolicyViolationLicenseView;
import com.synopsys.integration.blackduck.api.manual.view.PolicyViolationVulnerabilityView;

public class RapidScanResultConverter {
    public Map<String, RapidScanComponentDetail> convert(List<DeveloperScanComponentResultView> results) {
        Map<String, RapidScanComponentDetail> componentDetails = new HashMap<>();
        for (DeveloperScanComponentResultView resultView : results) {
            String componentName = resultView.getComponentName();
            RapidScanComponentDetail componentDetail = componentDetails.computeIfAbsent(componentName,
                ignoredKey -> createDetail(resultView));
            RapidScanComponentGroupDetail componentGroupDetail = componentDetail.getComponentDetails();
            RapidScanComponentGroupDetail securityGroupDetail = componentDetail.getSecurityDetails();
            RapidScanComponentGroupDetail licenseGroupDetail = componentDetail.getLicenseDetails();

            Set<String> policyNames = new LinkedHashSet<>(resultView.getViolatingPolicyNames());
            Set<PolicyViolationVulnerabilityView> vulnerabilityViolations = resultView.getPolicyViolationVulnerabilities();
            Set<PolicyViolationLicenseView> licenseViolations = resultView.getPolicyViolationLicenses();
            Set<String> vulnerabilityPolicyNames = vulnerabilityViolations.stream()
                                                       .map(PolicyViolationVulnerabilityView::getViolatingPolicyNames)
                                                       .flatMap(Collection::stream)
                                                       .collect(Collectors.toSet());

            Set<String> licensePolicyNames = licenseViolations.stream()
                                                 .map(PolicyViolationLicenseView::getViolatingPolicyNames)
                                                 .flatMap(Collection::stream)
                                                 .collect(Collectors.toSet());
            policyNames.removeAll(vulnerabilityPolicyNames);
            policyNames.removeAll(licensePolicyNames);

            componentGroupDetail.addPolicies(policyNames);
            securityGroupDetail.addPolicies(vulnerabilityPolicyNames);
            licenseGroupDetail.addPolicies(licensePolicyNames);

            boolean hasComponentErrors = StringUtils.isNotBlank(resultView.getErrorMessage());
            boolean hasComponentWarnings = StringUtils.isNotBlank(resultView.getWarningMessage());
            if (hasComponentErrors) {
                componentGroupDetail.addError(resultView.getErrorMessage());
            }

            if (hasComponentWarnings) {
                componentGroupDetail.addWarning(resultView.getWarningMessage());
            }

            if (!vulnerabilityPolicyNames.isEmpty()) {
                addVulnerabilityData(vulnerabilityViolations, securityGroupDetail);
            }

            if (!licensePolicyNames.isEmpty()) {
                addLicenseData(licenseViolations, licenseGroupDetail);
            }
        }
        return componentDetails;
    }

    public RapidScanResultSummary convertToSummary(Collection<RapidScanComponentDetail> details) {
        RapidScanResultSummary.Builder builder = new RapidScanResultSummary.Builder();
        for (RapidScanComponentDetail detail : details) {
            String formattedComponentName = String.format("%s %s (%s)", detail.getComponent(), detail.getVersion(), detail.getComponentIdentifier());
            if (detail.hasWarnings()) {
                builder.addComponentsViolatingPolicy(formattedComponentName);
            }
            if (detail.hasErrors()) {
                builder.addComponentViolatingPolicyWarnings(formattedComponentName);
            }
            builder.addPolicyViolationErrors(detail.getComponentDetails().getPolicyNames());
            builder.addPolicyViolations(detail.getComponentErrors());
            builder.addSecurityErrors(detail.getSecurityErrors());
            builder.addLicenseErrors(detail.getLicenseErrors());
            builder.addPolicyViolationWarnings(detail.getComponentWarnings());
            builder.addSecurityWarnings(detail.getSecurityWarnings());
            builder.addLicenseWarnings(detail.getLicenseWarnings());
        }

        return builder.build();
    }

    private RapidScanComponentDetail createDetail(DeveloperScanComponentResultView view) {
        String componentName = view.getComponentName();
        String componentVersion = view.getVersionName();
        String componentIdentifier = view.getComponentIdentifier();
        RapidScanComponentGroupDetail componentGroupDetail = new RapidScanComponentGroupDetail(RapidScanDetailGroup.POLICY);
        RapidScanComponentGroupDetail securityGroupDetail = new RapidScanComponentGroupDetail(RapidScanDetailGroup.SECURITY);
        RapidScanComponentGroupDetail licenseGroupDetail = new RapidScanComponentGroupDetail(RapidScanDetailGroup.LICENSE);

        return new RapidScanComponentDetail(componentName, componentVersion, componentIdentifier, componentGroupDetail, securityGroupDetail, licenseGroupDetail);
    }

    private void addVulnerabilityData(Set<PolicyViolationVulnerabilityView> vulnerabilities, RapidScanComponentGroupDetail securityDetail) {
        for (PolicyViolationVulnerabilityView vulnerabilityPolicyViolation : vulnerabilities) {
            boolean hasError = StringUtils.isNotBlank(vulnerabilityPolicyViolation.getErrorMessage());
            boolean hasWarning = StringUtils.isNotBlank(vulnerabilityPolicyViolation.getWarningMessage());
            if (hasError) {
                securityDetail.addError(vulnerabilityPolicyViolation.getErrorMessage());
            }

            if (hasWarning) {
                securityDetail.addWarning(vulnerabilityPolicyViolation.getWarningMessage());
            }
        }
    }

    private void addLicenseData(Set<PolicyViolationLicenseView> licenses, RapidScanComponentGroupDetail licenseDetail) {
        for (PolicyViolationLicenseView licensePolicyViolation : licenses) {
            boolean hasError = StringUtils.isNotBlank(licensePolicyViolation.getErrorMessage());
            boolean hasWarning = StringUtils.isNotBlank(licensePolicyViolation.getWarningMessage());
            if (hasError) {
                licenseDetail.addError(licensePolicyViolation.getErrorMessage());
            }

            if (hasWarning) {
                licenseDetail.addWarning(licensePolicyViolation.getWarningMessage());
            }
        }
    }
}
