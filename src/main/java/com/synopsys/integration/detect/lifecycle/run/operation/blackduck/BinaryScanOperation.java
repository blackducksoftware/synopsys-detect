/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import java.util.Optional;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.common.util.finder.WildcardFileFinder;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanToolResult;
import com.synopsys.integration.detect.tool.binaryscanner.BlackDuckBinaryScannerTool;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BinaryScanOperation {
    private final BlackDuckRunData blackDuckRunData;
    private final BinaryScanOptions binaryScanOptions;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DirectoryManager directoryManager;
    private final CodeLocationNameManager codeLocationNameManager;
    private final OperationSystem operationSystem;

    public BinaryScanOperation(BlackDuckRunData blackDuckRunData, BinaryScanOptions binaryScanOptions, StatusEventPublisher statusEventPublisher, ExitCodePublisher exitCodePublisher, DirectoryManager directoryManager,
        CodeLocationNameManager codeLocationNameManager, OperationSystem operationSystem) {
        this.blackDuckRunData = blackDuckRunData;
        this.binaryScanOptions = binaryScanOptions;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.directoryManager = directoryManager;
        this.codeLocationNameManager = codeLocationNameManager;
        this.operationSystem = operationSystem;
    }

    public Optional<CodeLocationCreationData<BinaryScanBatchOutput>> execute(NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws DetectUserFriendlyException, IntegrationException {
        Optional<CodeLocationCreationData<BinaryScanBatchOutput>> operationResult = Optional.empty();
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
        BlackDuckBinaryScannerTool binaryScannerTool = new BlackDuckBinaryScannerTool(statusEventPublisher, exitCodePublisher, codeLocationNameManager, directoryManager, new WildcardFileFinder(), binaryScanOptions,
            blackDuckServicesFactory.createBinaryScanUploadService(), operationSystem);

        BinaryScanToolResult result = binaryScannerTool.performBinaryScanActions(dockerTargetData, projectNameVersion);
        if (result.isSuccessful()) {
            operationResult = Optional.of(result.getCodeLocationCreationData());
        }

        return operationResult;
    }
}
