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
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanAggregateResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultAggregator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.detect.workflow.file.DetectFileUtils;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckRapidModePostActions {
    private static final String OPERATION_NAME = "Black Duck Rapid Scan Result Processing";
    private final IntLogger logger;
    private final Gson gson;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DirectoryManager directoryManager;
    private final OperationSystem operationSystem;
    private final RapidScanResultAggregator rapidScanResultAggregator;

    public BlackDuckRapidModePostActions(IntLogger logger, Gson gson, StatusEventPublisher statusEventPublisher, ExitCodePublisher exitCodePublisher, DirectoryManager directoryManager, OperationSystem operationSystem) {
        this.logger = logger;
        this.gson = gson;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.directoryManager = directoryManager;
        this.operationSystem = operationSystem;
        this.rapidScanResultAggregator = new RapidScanResultAggregator();
    }

    public void perform(NameVersion projectNameVersion, List<DeveloperScanComponentResultView> results) throws DetectUserFriendlyException {
        operationSystem.beginOperation(OPERATION_NAME);
        File jsonFile = generateJSONScanOutput(projectNameVersion, results);
        RapidScanAggregateResult aggregateResult = rapidScanResultAggregator.aggregateData(results);
        logger.info(String.format("%s:", RapidScanDetectResult.RAPID_SCAN_RESULT_DETAILS_HEADING));
        aggregateResult.logResult(logger);
        try {
            RapidScanResultSummary summary = aggregateResult.getSummary();
            statusEventPublisher.publishDetectResult(new RapidScanDetectResult(jsonFile.getCanonicalPath(), summary));
            if (summary.hasErrors()) {
                exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_POLICY_VIOLATION, createViolationMessage(summary.getPolicyViolationNames()));
            }
            operationSystem.completeWithSuccess(OPERATION_NAME);
        } catch (IOException ex) {
            logger.error("Rapid Scan Error", ex);
            operationSystem.completeWithError(OPERATION_NAME, ex.getMessage());
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
                logger.warn(String.format("Unable to delete an already-existing Black Duck Rapid Scan Result file: %s", jsonScanFile.getAbsoluteFile()));
                logger.error(ex);
            }
        }

        String jsonString = gson.toJson(results);
        logger.trace("Rapid Scan JSON result output: ");
        logger.trace(String.format("%s", jsonString));
        try {
            DetectFileUtils.writeToFile(jsonScanFile, jsonString);
        } catch (IOException ex) {
            String errorReason = "Cannot create rapid scan output file";
            operationSystem.completeWithError(OPERATION_NAME, errorReason, ex.getMessage());
            throw new DetectUserFriendlyException(errorReason, ex, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
        return jsonScanFile;
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
