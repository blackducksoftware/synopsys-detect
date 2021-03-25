/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

public class RapidScanAggregateResult {
    private final RapidScanResultSummary summary;
    private final RapidScanComponentGroupDetail componentDetails;
    private final RapidScanComponentGroupDetail securityDetails;
    private final RapidScanComponentGroupDetail licenseDetails;

    public RapidScanAggregateResult(RapidScanResultSummary summary, RapidScanComponentGroupDetail componentDetails, RapidScanComponentGroupDetail securityDetails,
        RapidScanComponentGroupDetail licenseDetails) {
        this.summary = summary;
        this.componentDetails = componentDetails;
        this.securityDetails = securityDetails;
        this.licenseDetails = licenseDetails;
    }

    public RapidScanResultSummary getSummary() {
        return summary;
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
}
