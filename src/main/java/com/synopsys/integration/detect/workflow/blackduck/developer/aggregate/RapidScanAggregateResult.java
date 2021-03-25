/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import com.synopsys.integration.log.IntLogger;

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

    public void logResult(IntLogger logger) {
        logGroupDetail(logger, componentDetails);
        logGroupDetail(logger, securityDetails);
        logGroupDetail(logger, licenseDetails);
    }

    private void logGroupDetail(IntLogger logger, RapidScanComponentGroupDetail groupDetail) {
        String groupName = groupDetail.getGroupName();
        logger.info("");
        logger.info(String.format("\t%s Errors: ", groupName));
        for (String message : groupDetail.getErrorMessages()) {
            logger.info(String.format("\t\t%s", message));
        }
        logger.info("");
        logger.info(String.format("\t%s Warnings: ", groupName));
        for (String message : groupDetail.getWarningMessages()) {
            logger.info(String.format("\t\t%s", message));
        }
    }
}
