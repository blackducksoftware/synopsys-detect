/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanOutput;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.input.CodeLocationInput;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanToolResult;
import com.synopsys.integration.detect.tool.binaryscanner.BlackDuckBinaryScannerTool;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;
import com.synopsys.integration.exception.IntegrationException;

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

    public void execute(CodeLocationInput codeLocationInput) throws DetectUserFriendlyException, IntegrationException {
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
        BlackDuckBinaryScannerTool binaryScannerTool = new BlackDuckBinaryScannerTool(eventSystem, codeLocationNameManager, directoryManager, new WildcardFileFinder(), binaryScanOptions,
            blackDuckServicesFactory.createBinaryScanUploadService());
        if (binaryScannerTool.shouldRun()) {
            BinaryScanToolResult result = binaryScannerTool.performBinaryScanActions(codeLocationInput.getNameVersion());
            if (result.isSuccessful()) {
                CodeLocationAccumulator<BinaryScanOutput, BinaryScanBatchOutput> accumulator = codeLocationInput.getCodeLocationAccumulator();
                accumulator.addWaitableCodeLocation(result.getCodeLocationCreationData());
            }
        }
    }
}
