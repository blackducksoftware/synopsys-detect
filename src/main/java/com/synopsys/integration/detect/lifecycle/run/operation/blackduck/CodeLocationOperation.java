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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdio2upload.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.DetectBdioUploadService;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.exception.IntegrationException;

public class CodeLocationOperation {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CodeLocationAccumulator<UploadOutput, UploadBatchOutput> execute(BlackDuckRunData blackDuckRunData, BdioResult bdioResult) throws DetectUserFriendlyException, IntegrationException {
        CodeLocationAccumulator<UploadOutput, UploadBatchOutput> codeLocationAccumulator = new CodeLocationAccumulator<>();
        List<UploadTarget> uploadTargetList = bdioResult.getUploadTargets();
        if (!uploadTargetList.isEmpty()) {
            logger.info(String.format("Created %d BDIO files.", bdioResult.getUploadTargets().size()));
            if (blackDuckRunData.isOnline()) {
                logger.debug("Uploading BDIO files.");
                BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
                BdioUploadService bdioUploadService = blackDuckServicesFactory.createBdioUploadService();
                Bdio2UploadService bdio2UploadService = blackDuckServicesFactory.createBdio2UploadService();
                DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService();
                logger.info(String.format("Created %d BDIO files.", uploadTargetList.size()));
                logger.debug("Uploading BDIO files.");
                CodeLocationCreationData<UploadBatchOutput> uploadBatchOutputCodeLocationCreationData = detectBdioUploadService.uploadBdioFiles(bdioResult, bdioUploadService,
                    bdio2UploadService);
                codeLocationAccumulator.addWaitableCodeLocation(uploadBatchOutputCodeLocationCreationData);
            }
        } else {
            logger.debug("Did not create any BDIO files.");
        }
        return codeLocationAccumulator;
    }
}
