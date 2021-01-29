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

import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdio2upload.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationResult;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.DetectBdioUploadService;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.exception.IntegrationException;

public class CodeLocationOperation extends BlackDuckOnlineOperation<BdioResult, CodeLocationAccumulator> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BdioUploadService bdioUploadService;
    private final Bdio2UploadService bdio2UploadService;
    private final DetectBdioUploadService detectBdioUploadService;

    public CodeLocationOperation(ProductRunData productRunData, @Nullable BdioUploadService bdioUploadService, @Nullable Bdio2UploadService bdio2UploadService, @Nullable DetectBdioUploadService detectBdioUploadService) {
        super(productRunData);
        this.bdioUploadService = bdioUploadService;
        this.bdio2UploadService = bdio2UploadService;
        this.detectBdioUploadService = detectBdioUploadService;
    }

    @Override
    protected boolean shouldExecute() {
        return super.shouldExecute() && null != detectBdioUploadService && null != bdioUploadService && null != bdio2UploadService;
    }

    @Override
    public String getOperationName() {
        return "Create Code Locations";
    }

    @Override
    protected OperationResult<CodeLocationAccumulator> executeOperation(BdioResult input) throws DetectUserFriendlyException, IntegrationException {
        CodeLocationAccumulator codeLocationAccumulator = new CodeLocationAccumulator();
        List<UploadTarget> uploadTargetList = input.getUploadTargets();
        if (!uploadTargetList.isEmpty()) {
            logger.info(String.format("Created %d BDIO files.", uploadTargetList.size()));
            logger.debug("Uploading BDIO files.");
            CodeLocationCreationData<UploadBatchOutput> uploadBatchOutputCodeLocationCreationData = detectBdioUploadService.uploadBdioFiles(input, bdioUploadService,
                bdio2UploadService);
            codeLocationAccumulator.addWaitableCodeLocation(uploadBatchOutputCodeLocationCreationData);
        } else {
            logger.debug("Did not create any BDIO files.");
        }
        return OperationResult.success(codeLocationAccumulator);
    }
}
