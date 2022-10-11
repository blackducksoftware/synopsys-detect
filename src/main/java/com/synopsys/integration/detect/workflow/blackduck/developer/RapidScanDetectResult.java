package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.LinkedList;
import java.util.List;

import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanDetailGroup;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.detect.workflow.result.DetectResult;

public class RapidScanDetectResult implements DetectResult {
    public static final String NONPERSISTENT_SCAN_RESULT_HEADING = System.getProperty("com.synopsys.nonpersistent.scan.mode.string") + " Scan Result";
    public static final String NONPERSISTENT_SCAN_RESULT_DETAILS_HEADING = System.getProperty("com.synopsys.nonpersistent.scan.mode.string") + " Scan Result Details";
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
        return String.format("%s: (for more detail look in the log for %s)", NONPERSISTENT_SCAN_RESULT_HEADING, NONPERSISTENT_SCAN_RESULT_DETAILS_HEADING);
    }

    @Override
    public List<String> getResultSubMessages() {
        return subMessages;
    }

    private List<String> createResultMessages(RapidScanResultSummary summary) {
        String policyGroupName = RapidScanDetailGroup.POLICY.getDisplayName();
        String securityGroupName = RapidScanDetailGroup.SECURITY.getDisplayName();
        String licenseGroupName = RapidScanDetailGroup.LICENSE.getDisplayName();
        String countFormat = "\t\t* %s: %d";
        String indentedMessageFormat = "\t\t%s";

        List<String> resultMessages = new LinkedList<>();
        resultMessages.add("");
        resultMessages.add("\tCritical and blocking policy violations for");
        resultMessages.add(String.format(countFormat, policyGroupName, summary.getPolicyErrorCount()));
        resultMessages.add(String.format(countFormat, securityGroupName, summary.getSecurityErrorCount()));
        resultMessages.add(String.format(countFormat, licenseGroupName, summary.getLicenseErrorCount()));
        resultMessages.add("");
        resultMessages.add("\tOther policy violations");
        resultMessages.add(String.format(countFormat, policyGroupName, summary.getPolicyWarningCount()));
        resultMessages.add(String.format(countFormat, securityGroupName, summary.getSecurityWarningCount()));
        resultMessages.add(String.format(countFormat, licenseGroupName, summary.getLicenseWarningCount()));
        resultMessages.add("");
        resultMessages.add("\tPolicies Violated:");
        summary.getPolicyViolationNames().stream()
            .sorted()
            .forEach(policy -> resultMessages.add(String.format(indentedMessageFormat, policy)));
        resultMessages.add("");
        resultMessages.add("\tComponents with Policy Violations:");
        summary.getComponentsViolatingPolicy().stream()
            .sorted()
            .forEach(component -> resultMessages.add(String.format(indentedMessageFormat, component)));
        resultMessages.add("");
        resultMessages.add("\tComponents with Policy Violation Warnings:");
        summary.getComponentsViolatingPolicyWarnings().stream()
            .sorted()
            .forEach(component -> resultMessages.add(String.format(indentedMessageFormat, component)));
        return resultMessages;
    }
}
