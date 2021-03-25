/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.LinkedHashSet;
import java.util.Set;

public class RapidScanResultSummary {
    private final int policyErrorCount;
    private final int policyWarningCount;
    private final int securityErrorCount;
    private final int securityWarningCount;
    private final int licenseErrorCount;
    private final int licenseWarningCount;

    private final Set<String> policyViolationNames;
    private final Set<String> componentsViolatingPolicy;
    private final Set<String> componentsViolatingPolicyWarnings;

    private RapidScanResultSummary(int policyErrorCount, int policyWarningCount, int securityErrorCount, int securityWarningCount, int licenseErrorCount, int licenseWarningCount,
        Set<String> policyViolationNames, Set<String> componentsViolatingPolicy, Set<String> componentsViolatingPolicyWarnings) {
        this.policyErrorCount = policyErrorCount;
        this.policyWarningCount = policyWarningCount;
        this.securityErrorCount = securityErrorCount;
        this.securityWarningCount = securityWarningCount;
        this.licenseErrorCount = licenseErrorCount;
        this.licenseWarningCount = licenseWarningCount;
        this.policyViolationNames = policyViolationNames;
        this.componentsViolatingPolicy = componentsViolatingPolicy;
        this.componentsViolatingPolicyWarnings = componentsViolatingPolicyWarnings;
    }

    public boolean hasErrors() {
        return policyErrorCount > 0 || securityErrorCount > 0 || licenseErrorCount > 0;
    }

    public int getPolicyErrorCount() {
        return policyErrorCount;
    }

    public int getPolicyWarningCount() {
        return policyWarningCount;
    }

    public int getSecurityErrorCount() {
        return securityErrorCount;
    }

    public int getSecurityWarningCount() {
        return securityWarningCount;
    }

    public int getLicenseErrorCount() {
        return licenseErrorCount;
    }

    public int getLicenseWarningCount() {
        return licenseWarningCount;
    }

    public Set<String> getPolicyViolationNames() {
        return policyViolationNames;
    }

    public Set<String> getComponentsViolatingPolicy() {
        return componentsViolatingPolicy;
    }

    public Set<String> getComponentsViolatingPolicyWarnings() {
        return componentsViolatingPolicyWarnings;
    }

    public static class Builder {
        private int policyErrors;
        private int policyWarnings;
        private int securityErrors;
        private int securityWarnings;
        private int licenseErrors;
        private int licenseWarnings;

        private Set<String> violatedPolicyNames;
        private Set<String> componentsViolatingPolicy;
        private Set<String> componentsViolatingPolicyWarnings;

        public Builder() {
            this.policyErrors = 0;
            this.policyWarnings = 0;
            this.securityErrors = 0;
            this.securityWarnings = 0;
            this.licenseErrors = 0;
            this.licenseWarnings = 0;

            this.violatedPolicyNames = new LinkedHashSet<>();
            this.componentsViolatingPolicy = new LinkedHashSet<>();
            this.componentsViolatingPolicyWarnings = new LinkedHashSet<>();
        }

        public void addPolicyViolations(int count) {
            policyErrors += count;
        }

        public void addPolicyViolationWarnings(int count) {
            policyWarnings += count;
        }

        public void addSecurityErrors(int count) {
            securityErrors += count;
        }

        public void addSecurityWarnings(int count) {
            securityWarnings += count;
        }

        public void addLicenseErrors(int count) {
            licenseErrors += count;
        }

        public void addLicenseWarnings(int count) {
            licenseWarnings += count;
        }

        public void addViolatedPolicyNames(Set<String> policyNames) {
            violatedPolicyNames.addAll(policyNames);
        }

        public void addComponentsViolatingPolicy(String componentName) {
            componentsViolatingPolicy.add(componentName);
        }

        public void addComponentViolatingPolicyWarnings(String componentName) {
            componentsViolatingPolicyWarnings.add(componentName);
        }

        public void addDetailData(RapidScanComponentDetail detail) {
            String formattedComponentName = String.format("%s %s (%s)", detail.getComponent(), detail.getVersion(), detail.getComponentIdentifier());
            if (detail.hasWarnings()) {
                addComponentsViolatingPolicy(formattedComponentName);
            }
            if (detail.hasErrors()) {
                addComponentViolatingPolicyWarnings(formattedComponentName);
            }

            addViolatedPolicyNames(detail.getComponentDetails().getPolicyNames());
            addPolicyViolations(detail.getComponentErrorCount());
            addSecurityErrors(detail.getSecurityErrorCount());
            addLicenseErrors(detail.getLicenseErrorCount());
            addPolicyViolationWarnings(detail.getComponentWarningCount());
            addSecurityWarnings(detail.getSecurityWarningCount());
            addLicenseWarnings(detail.getLicenseWarningCount());
        }

        public RapidScanResultSummary build() {
            return new RapidScanResultSummary(this.policyErrors, this.policyWarnings, this.securityErrors, this.securityWarnings,
                this.licenseErrors, this.licenseWarnings, violatedPolicyNames, componentsViolatingPolicy,
                componentsViolatingPolicyWarnings);
        }
    }
}
