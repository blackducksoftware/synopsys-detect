package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.log.IntLogger;

public class RapidScanAggregateResult {
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
        logTransitiveRubbish(logger);
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
    
    private void logTransitiveRubbish(IntLogger logger) {
        String groupName = "Upgrade Guidance For Transitive Components:";
        String componentMsgString = "component";
        logger.info("");
        logger.info(String.format("\t%s", groupName));
        for (String guidance : this.transitiveGuidance) {
            logger.info(String.format("\t\t%s",guidance));
        }
            
//        Set<String> componentKeys = this.directToTransitiveChildren.keySet();
//        for (String key : componentKeys) {
//            String plural = "s";
//
//            String externalId = key;
//            String componentName = /*externalId;*/this.externalIdToComponentName.get(externalId);
//            if (componentName == null) {
//                componentName = externalId;
//            }
//            componentName = externalId;
//            List<String> children = this.directToTransitiveChildren.get(externalId);
//            String childComponents = StringUtils.join(children, ", ");
//            if (children.size() == 1) {
//                plural = "";
//            }
//            
//            String versionsToUse = "";
//            String[] versions = this.directUpgradeGuidanceVersions.get(externalId);
//            if (versions[1] != null && versions[0] != null   && versions[1].equals(versions[0])) {
//                versionsToUse = "version " + versions[1];
//            } else {
//                versionsToUse = "versions (Short Term) " + versions[0] +", " + "(Long Term) " + versions[1];
//            }
//            
//            logger.info(String.format("\t\tUpgrade component %s to %s in order to upgrade transitive component%s %s", componentName, versionsToUse, plural, childComponents));
//
//            }
        
    }
}
