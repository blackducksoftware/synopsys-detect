package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.List;

import com.synopsys.integration.log.IntLogger;

public class RapidScanAggregateResult {
    
    private static final String DOUBLE_TAB = "\t\t%s";
    
    private final RapidScanResultSummary summary;
    private final RapidScanComponentGroupDetail componentDetails;
    private final RapidScanComponentGroupDetail securityDetails;
    private final RapidScanComponentGroupDetail licenseDetails;
    private final List<String> transitiveGuidance;

    public RapidScanAggregateResult(
        RapidScanResultSummary summary,
        RapidScanComponentGroupDetail componentDetails,
        RapidScanComponentGroupDetail securityDetails,
        RapidScanComponentGroupDetail licenseDetails,
        List<String> transitiveGuidance
    ) {
        this.summary = summary;
        this.componentDetails = componentDetails;
        this.securityDetails = securityDetails;
        this.licenseDetails = licenseDetails;
        this.transitiveGuidance = transitiveGuidance;
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
            logger.info(String.format(DOUBLE_TAB, message));
        }
        logger.info("");
        logger.info(String.format("\t%s Warnings: ", groupName));
        for (String message : groupDetail.getWarningMessages()) {
            logger.info(String.format(DOUBLE_TAB, message));
        }
    }
    
    private void logTransitiveGuidanceInformation(IntLogger logger) {
        String groupName = "Upgrade Guidance For Transitive Components:";
        String componentMsgString = "component";
        logger.info("");
        logger.info(String.format("\t%s", groupName));
        for (String guidance : this.transitiveGuidance) {
            logger.info(String.format(DOUBLE_TAB,guidance));
        }
    }
}
