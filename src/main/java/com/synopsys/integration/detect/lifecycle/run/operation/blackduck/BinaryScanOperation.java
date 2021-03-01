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
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanToolResult;
import com.synopsys.integration.detect.tool.binaryscanner.BlackDuckBinaryScannerTool;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BinaryScanOperation {

    private final BlackDuckRunData blackDuckRunData;
    private final BinaryScanOptions binaryScanOptions;
    private final EventSystem eventSystem;
    private final DirectoryManager directoryManager;
    private final CodeLocationNameManager codeLocationNameManager;

    public BinaryScanOperation(BlackDuckRunData blackDuckRunData, BinaryScanOptions binaryScanOptions, EventSystem eventSystem, DirectoryManager directoryManager,
        CodeLocationNameManager codeLocationNameManager) {
        this.blackDuckRunData = blackDuckRunData;
        this.binaryScanOptions = binaryScanOptions;
        this.eventSystem = eventSystem;
        this.directoryManager = directoryManager;
        this.codeLocationNameManager = codeLocationNameManager;
    }

    public Optional<CodeLocationCreationData<BinaryScanBatchOutput>> execute(NameVersion projectNameVersion) throws DetectUserFriendlyException, IntegrationException {
        Optional<CodeLocationCreationData<BinaryScanBatchOutput>> operationResult = Optional.empty();
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
        BlackDuckBinaryScannerTool binaryScannerTool = new BlackDuckBinaryScannerTool(eventSystem, codeLocationNameManager, directoryManager, new WildcardFileFinder(), binaryScanOptions,
            blackDuckServicesFactory.createBinaryScanUploadService());
        if (binaryScannerTool.shouldRun()) {
            BinaryScanToolResult result = binaryScannerTool.performBinaryScanActions(projectNameVersion);
            if (result.isSuccessful()) {
                operationResult = Optional.of(result.getCodeLocationCreationData());
            }
        }

        return operationResult;
    }
}
