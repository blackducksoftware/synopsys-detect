/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.blackduck;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadCodeLocationCreationRequest;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.exception.IntegrationException;

public class DetectBdioUploadService {
    private final Logger logger = LoggerFactory.getLogger(DetectBdioUploadService.class);

    private final DetectConfiguration detectConfiguration;
    private final BdioUploadService bdioUploadService;
    private final EventSystem eventSystem;

    public DetectBdioUploadService(final DetectConfiguration detectConfiguration, final BdioUploadService bdioUploadService, EventSystem eventSystem) {
        this.detectConfiguration = detectConfiguration;
        this.bdioUploadService = bdioUploadService;
        this.eventSystem = eventSystem;
    }

    public CodeLocationCreationData<UploadBatchOutput> uploadBdioFiles(List<UploadTarget> uploadTargets) throws IntegrationException, DetectUserFriendlyException {
        UploadBatch uploadBatch = new UploadBatch();
        for (UploadTarget uploadTarget : uploadTargets) {
            logger.info(String.format("uploading %s to %s", uploadTarget.getUploadFile().getName(), detectConfiguration.getProperty(DetectProperty.BLACKDUCK_URL, PropertyAuthority.None)));
            uploadBatch.addUploadTarget(uploadTarget);
        }

        BdioUploadCodeLocationCreationRequest uploadRequest = bdioUploadService.createUploadRequest(uploadBatch);
        CodeLocationCreationData<UploadBatchOutput> response = bdioUploadService.uploadBdio(uploadRequest);
        for (UploadOutput uploadOutput : response.getOutput()) {
            if (uploadOutput.getResult() == Result.FAILURE) {
                logger.error("Failed to upload code location: " + uploadOutput.getCodeLocationName());
                logger.error("Reason: " + uploadOutput.getErrorMessage().orElse("Unknown reason."));
                throw new DetectUserFriendlyException("An error occurred uploading a bdio file.", uploadOutput.getException().orElse(null), ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }
        }

        return response;
    }

}