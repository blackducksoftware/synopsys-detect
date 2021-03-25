/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.LinkedList;
import java.util.List;

import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanDetailGroup;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.detect.workflow.result.DetectResult;

public class RapidScanDetectResult implements DetectResult {
    public static final String RAPID_SCAN_RESULT_HEADING = "Rapid Scan Result";
    public static final String RAPID_SCAN_RESULT_DETAILS_HEADING = "Rapid Scan Result Details";
    private final String jsonFilePath;
    private final List<String> subMessages;

    public RapidScanDetectResult(String jsonFilePath, RapidScanResultSummary resultSummary) {
        this.jsonFilePath = jsonFilePath;
        this.subMessages = createResultMessages(resultSummary);
    }

    @Override
    public String getResultLocation() {
        return jsonFilePath;
    }

    @Override
    public String getResultMessage() {
        return String.format("%s: (for more detail look in the log for %s)", RAPID_SCAN_RESULT_HEADING, RAPID_SCAN_RESULT_DETAILS_HEADING);
    }

    @Override
    public List<String> getResultSubMessages() {
        return subMessages;
    }

    private List<String> createResultMessages(RapidScanResultSummary summary) {
        String policyGroupName = RapidScanDetailGroup.POLICY.getDisplayName();
        String securityGroupName = RapidScanDetailGroup.SECURITY.getDisplayName();
        String licenseGroupName = RapidScanDetailGroup.LICENSE.getDisplayName();
        List<String> resultMessages = new LinkedList<>();
        resultMessages.add("");
        resultMessages.add(String.format("\t%s Errors = %d", policyGroupName, summary.getPolicyErrorCount()));
        resultMessages.add(String.format("\t%s Warnings = %d", policyGroupName, summary.getPolicyWarningCount()));
        resultMessages.add(String.format("\t%s Errors = %d", securityGroupName, summary.getSecurityErrorCount()));
        resultMessages.add(String.format("\t%s Warnings = %d", securityGroupName, summary.getSecurityWarningCount()));
        resultMessages.add(String.format("\t%s Errors = %d", licenseGroupName, summary.getLicenseErrorCount()));
        resultMessages.add(String.format("\t%s Warnings = %d", licenseGroupName, summary.getLicenseWarningCount()));
        resultMessages.add("");
        resultMessages.add("\tPolicies Violated:");
        summary.getPolicyViolationNames().forEach(policy -> resultMessages.add(String.format("\t\t%s", policy)));
        resultMessages.add("");
        resultMessages.add("\tComponents with Policy Violations:");
        summary.getComponentsViolatingPolicy().forEach(component -> resultMessages.add(String.format("\t\t%s", component)));
        resultMessages.add("");
        resultMessages.add("\tComponents with Policy Violation Warnings:");
        summary.getComponentsViolatingPolicyWarnings().forEach(component -> resultMessages.add(String.format("\t\t%s", component)));
        return resultMessages;
    }
}
