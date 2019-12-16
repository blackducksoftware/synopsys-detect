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

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;

public class DetectBdioUploadService {
    private final Logger logger = LoggerFactory.getLogger(DetectBdioUploadService.class);

    private final DetectConfiguration detectConfiguration;

    public DetectBdioUploadService(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public CodeLocationCreationData<UploadBatchOutput> uploadBdioFiles(final List<UploadTarget> uploadTargets, final BdioUploader bdioUploader) throws DetectUserFriendlyException, IntegrationException {
        final UploadBatch uploadBatch = new UploadBatch();
        for (final UploadTarget uploadTarget : uploadTargets) {
            logger.debug(String.format("Uploading %s to %s", uploadTarget.getUploadFile().getName(), detectConfiguration.getProperty(DetectProperty.BLACKDUCK_URL, PropertyAuthority.NONE)));
            uploadBatch.addUploadTarget(uploadTarget);
        }

        final CodeLocationCreationData<UploadBatchOutput> response = bdioUploader.uploadBdio(uploadBatch);
        for (final UploadOutput uploadOutput : response.getOutput()) {
            if (uploadOutput.getResult() == Result.FAILURE) {
                logger.error(String.format("Failed to upload code location: %s", uploadOutput.getCodeLocationName()));
                logger.error(String.format("Reason: %s", uploadOutput.getErrorMessage().orElse("Unknown reason.")));
                throw new DetectUserFriendlyException("An error occurred uploading a bdio file.", uploadOutput.getException().orElse(null), ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }
        }

        return response;
    }

    @FunctionalInterface
    public interface BdioUploader {
        CodeLocationCreationData<UploadBatchOutput> uploadBdio(final UploadBatch uploadBatch) throws IntegrationException;
    }
}