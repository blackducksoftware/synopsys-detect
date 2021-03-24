/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.workflow.file.DetectFileUtils;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckRapidModePostActions {
    private static final String OPERATION_NAME = "Black Duck Rapid Scan Result Processing";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Gson gson;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DirectoryManager directoryManager;
    private final OperationSystem operationSystem;
    private final RapidScanResultConverter rapidScanResultConverter;

    public BlackDuckRapidModePostActions(Gson gson, StatusEventPublisher statusEventPublisher, ExitCodePublisher exitCodePublisher, DirectoryManager directoryManager, OperationSystem operationSystem) {
        this.gson = gson;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.directoryManager = directoryManager;
        this.operationSystem = operationSystem;
        this.rapidScanResultConverter = new RapidScanResultConverter();
    }

    public void perform(NameVersion projectNameVersion, List<DeveloperScanComponentResultView> results) throws DetectUserFriendlyException {
        operationSystem.beginOperation(OPERATION_NAME);
        File jsonFile = generateJSONScanOutput(projectNameVersion, results);
        Map<String, RapidScanComponentDetail> componentDetails = rapidScanResultConverter.convert(results);
        RapidScanResultSummary summary = rapidScanResultConverter.convertToSummary(componentDetails.values());
        List<String> summaryMessages = createResultMessages(summary);
        logger.info("{}:", RapidScanDetectResult.RAPID_SCAN_RESULT_DETAILS_HEADING);
        logDetailedInformation(componentDetails);
        try {
            statusEventPublisher.publishDetectResult(new RapidScanDetectResult(jsonFile.getCanonicalPath(), summaryMessages));
            if (summary.hasErrors()) {
                exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_POLICY_VIOLATION, createViolationMessage(summary.getPolicyViolationNames()));
            }
            operationSystem.completeWithSuccess(OPERATION_NAME);
        } catch (IOException ex) {
            logger.error("Rapid Scan Error", ex);
            operationSystem.completeWithError(OPERATION_NAME, ex.getMessage());
        }
    }

    private void logDetailedInformation(Map<String, RapidScanComponentDetail> componentDetails) {
        List<String> sortedKeys = componentDetails.keySet().stream()
                                      .sorted()
                                      .collect(Collectors.toList());
        Map<RapidScanDetailGroup, RapidScanComponentGroupDetail> aggregatedDetails = new HashMap<>();
        for (String key : sortedKeys) {
            RapidScanComponentDetail detail = componentDetails.get(key);
            RapidScanDetailGroup securityGroupName = detail.getSecurityDetails().getGroup();
            RapidScanDetailGroup licenseGroupName = detail.getLicenseDetails().getGroup();
            RapidScanDetailGroup componentGroupName = detail.getComponentDetails().getGroup();
            RapidScanComponentGroupDetail aggregatedSecurityDetail = aggregatedDetails.computeIfAbsent(detail.getSecurityDetails().getGroup(), ignoredKey -> new RapidScanComponentGroupDetail(securityGroupName));
            RapidScanComponentGroupDetail aggregatedLicenseDetail = aggregatedDetails.computeIfAbsent(detail.getLicenseDetails().getGroup(), ignoredKey -> new RapidScanComponentGroupDetail(licenseGroupName));
            RapidScanComponentGroupDetail aggregatedComponentDetail = aggregatedDetails.computeIfAbsent(detail.getComponentDetails().getGroup(), ignoredKey -> new RapidScanComponentGroupDetail(componentGroupName));
            aggregatedComponentDetail.addErrors(detail.getComponentDetails().getErrorMessages());
            aggregatedComponentDetail.addWarnings(detail.getComponentDetails().getWarningMessages());
            aggregatedSecurityDetail.addErrors(detail.getSecurityDetails().getErrorMessages());
            aggregatedSecurityDetail.addWarnings(detail.getSecurityDetails().getWarningMessages());
            aggregatedLicenseDetail.addErrors(detail.getLicenseDetails().getErrorMessages());
            aggregatedLicenseDetail.addWarnings(detail.getLicenseDetails().getWarningMessages());
        }

        // now log aggregated data
        logGroupDetail(aggregatedDetails.get(RapidScanDetailGroup.POLICY));
        logGroupDetail(aggregatedDetails.get(RapidScanDetailGroup.SECURITY));
        logGroupDetail(aggregatedDetails.get(RapidScanDetailGroup.LICENSE));
    }

    private void logGroupDetail(RapidScanComponentGroupDetail groupDetail) {
        String groupName = groupDetail.getGroupName();
        logger.info("");
        logger.info("\t{} Errors: ", groupName);
        for (String message : groupDetail.getErrorMessages()) {
            logger.info("\t\t{}", message);
        }
        logger.info("");
        logger.info("\t{} Warnings: ", groupName);
        for (String message : groupDetail.getWarningMessages()) {
            logger.info("\t\t{}", message);
        }
    }

    private File generateJSONScanOutput(NameVersion projectNameVersion, List<DeveloperScanComponentResultView> results) throws DetectUserFriendlyException {
        IntegrationEscapeUtil escapeUtil = new IntegrationEscapeUtil();
        String escapedProjectName = escapeUtil.replaceWithUnderscore(projectNameVersion.getName());
        String escapedProjectVersionName = escapeUtil.replaceWithUnderscore(projectNameVersion.getVersion());
        File jsonScanFile = new File(directoryManager.getScanOutputDirectory(), escapedProjectName + "_" + escapedProjectVersionName + "_BlackDuck_DeveloperMode_Result.json");
        if (jsonScanFile.exists()) {
            try {
                Files.delete(jsonScanFile.toPath());
            } catch (IOException ex) {
                logger.warn("Unable to delete an already-existing Black Duck Rapid Scan Result file: {}", jsonScanFile.getAbsoluteFile(), ex);
            }
        }

        String jsonString = gson.toJson(results);
        logger.trace("Rapid Scan JSON result output: ");
        logger.trace("{}", jsonString);
        try {
            DetectFileUtils.writeToFile(jsonScanFile, jsonString);
        } catch (IOException ex) {
            String errorReason = "Cannot create rapid scan output file";
            operationSystem.completeWithError(OPERATION_NAME, errorReason, ex.getMessage());
            throw new DetectUserFriendlyException(errorReason, ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
        return jsonScanFile;
    }

    private List<String> createResultMessages(RapidScanResultSummary summary) {
        List<String> resultMessages = new LinkedList<>();
        resultMessages.add("");
        resultMessages.add(String.format("\tPolicy Errors = %d", summary.getPolicyErrorCount()));
        resultMessages.add(String.format("\tPolicy Warning = %d", summary.getPolicyWarningCount()));
        resultMessages.add(String.format("\tSecurity Errors = %d", summary.getSecurityErrorCount()));
        resultMessages.add(String.format("\tSecurity Warnings = %d", summary.getSecurityWarningCount()));
        resultMessages.add(String.format("\tLicense Errors = %d", summary.getLicenseErrorCount()));
        resultMessages.add(String.format("\tLicense Warnings = %d", summary.getLicenseWarningCount()));
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

    private String createViolationMessage(Set<String> violatedPolicyNames) {
        StringBuilder stringBuilder = new StringBuilder(200);
        stringBuilder.append("Black Duck found:");
        stringBuilder.append(fixComponentPlural(" %d %s in violation", violatedPolicyNames.size()));
        return stringBuilder.toString();
    }

    private String fixComponentPlural(String formatString, int count) {
        String label = "components";
        if (count == 1)
            label = "component";
        return String.format(formatString, count, label);
    }
}
