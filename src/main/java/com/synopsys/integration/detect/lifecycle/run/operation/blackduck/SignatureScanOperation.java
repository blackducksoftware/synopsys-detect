/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import java.util.Arrays;
import java.util.Optional;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.input.SignatureScanInput;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerTool;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerToolResult;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.exception.IntegrationException;

public class SignatureScanOperation {
    private final BlackDuckRunData blackDuckRunData;
    private final BlackDuckSignatureScannerTool signatureScannerTool;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;

    public SignatureScanOperation(BlackDuckRunData blackDuckRunData, BlackDuckSignatureScannerTool signatureScannerTool,
        StatusEventPublisher statusEventPublisher, ExitCodePublisher exitCodePublisher) {
        this.blackDuckRunData = blackDuckRunData;
        this.signatureScannerTool = signatureScannerTool;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
    }

    public Optional<CodeLocationCreationData<ScanBatchOutput>> execute(SignatureScanInput signatureScanInput) throws DetectUserFriendlyException, IntegrationException {
        Optional<CodeLocationCreationData<ScanBatchOutput>> result = Optional.empty();
        BlackDuckServerConfig blackDuckServerConfig = null;
        CodeLocationCreationService codeLocationCreationService = null;
        if (null != blackDuckRunData && blackDuckRunData.isOnline()) {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            codeLocationCreationService = blackDuckServicesFactory.createCodeLocationCreationService();
            blackDuckServerConfig = blackDuckRunData.getBlackDuckServerConfig();
        }
        SignatureScannerToolResult signatureScannerToolResult = signatureScannerTool.runScanTool(codeLocationCreationService, blackDuckServerConfig, signatureScanInput.getProjectNameVersion(), signatureScanInput.getDockerTar());
        if (signatureScannerToolResult.getResult() == Result.SUCCESS && signatureScannerToolResult.getCreationData().isPresent()) {
            result = signatureScannerToolResult.getCreationData();
        } else if (signatureScannerToolResult.getResult() != Result.SUCCESS) {
            statusEventPublisher.publishStatusSummary(new Status("SIGNATURE_SCAN", StatusType.FAILURE));
            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.SIGNATURE_SCANNER, Arrays.asList(signatureScannerToolResult.getResult().toString())));
        }
        return result;
    }
}
