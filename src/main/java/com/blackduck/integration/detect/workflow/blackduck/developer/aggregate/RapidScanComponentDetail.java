package com.blackduck.integration.detect.workflow.blackduck.developer.aggregate;

public class RapidScanComponentDetail {
    private final String component;
    private final String version;
    private final String componentIdentifier;
    private final RapidScanComponentGroupDetail componentDetails;
    private final RapidScanComponentGroupDetail securityDetails;
    private final RapidScanComponentGroupDetail licenseDetails;
    private final RapidScanComponentGroupDetail violatingPoliciesDetails;

    public RapidScanComponentDetail(
        String component,
        String version,
        String componentIdentifier,
        RapidScanComponentGroupDetail componentDetails,
        RapidScanComponentGroupDetail securityDetails,
        RapidScanComponentGroupDetail licenseDetails,
        RapidScanComponentGroupDetail violatingPoliciesDetails
    ) {
        this.component = component;
        this.version = version;
        this.componentIdentifier = componentIdentifier;
        this.componentDetails = componentDetails;
        this.securityDetails = securityDetails;
        this.licenseDetails = licenseDetails;
        this.violatingPoliciesDetails = violatingPoliciesDetails;
    }

    public boolean hasErrors() {
        return componentDetails.hasErrors() || securityDetails.hasErrors() || licenseDetails.hasErrors() || violatingPoliciesDetails.hasErrors();
    }

    public boolean hasWarnings() {
        return componentDetails.hasWarnings() || securityDetails.hasWarnings() || licenseDetails.hasWarnings() || violatingPoliciesDetails.hasWarnings();
    }

    public String getComponent() {
        return component;
    }

    public String getVersion() {
        return version;
    }

    public String getComponentIdentifier() {
        return componentIdentifier;
    }

    public RapidScanComponentGroupDetail getComponentDetails() {
        return componentDetails;
    }

    public RapidScanComponentGroupDetail getSecurityDetails() {
        return securityDetails;
    }

    public RapidScanComponentGroupDetail getLicenseDetails() {
        return licenseDetails;
    }

    public RapidScanComponentGroupDetail getViolatingPoliciesDetails() { return violatingPoliciesDetails; }

    public int getComponentErrorCount() {
        return getGroupErrorCount(componentDetails);
    }

    public int getComponentWarningCount() {
        return getGroupWarningCount(componentDetails);
    }

    public int getSecurityErrorCount() {
        return getGroupErrorCount(securityDetails);
    }

    public int getSecurityWarningCount() {
        return getGroupWarningCount(securityDetails);
    }

    public int getLicenseErrorCount() {
        return getGroupErrorCount(licenseDetails);
    }

    public int getLicenseWarningCount() {
        return getGroupWarningCount(licenseDetails);
    }

    public int getAllViolatingPoliciesErrorCount() { return getGroupErrorCount(violatingPoliciesDetails); }

    public int getAllViolatingPoliciesWarningCount() { return getGroupWarningCount(violatingPoliciesDetails); }

    private int getGroupErrorCount(RapidScanComponentGroupDetail groupDetail) {
        return groupDetail.getErrorMessages().size();
    }

    private int getGroupWarningCount(RapidScanComponentGroupDetail groupDetail) {
        return groupDetail.getWarningMessages().size();
    }
}
