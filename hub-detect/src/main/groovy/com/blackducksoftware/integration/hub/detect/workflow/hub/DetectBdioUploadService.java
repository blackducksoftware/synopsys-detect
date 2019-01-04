/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadCodeLocationCreationRequest;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.exception.IntegrationException;

public class DetectBdioUploadService {
    private final Logger logger = LoggerFactory.getLogger(DetectBdioUploadService.class);

    private final DetectConfiguration detectConfiguration;
    private final BdioUploadService bdioUploadService;

    public DetectBdioUploadService(final DetectConfiguration detectConfiguration, final BdioUploadService bdioUploadService) {
        this.detectConfiguration = detectConfiguration;
        this.bdioUploadService = bdioUploadService;
    }

    public CodeLocationCreationData<UploadBatchOutput> uploadBdioFiles(List<UploadTarget> uploadTargets) throws IntegrationException {
        UploadBatch uploadBatch = new UploadBatch();
        for (UploadTarget uploadTarget : uploadTargets) {
            logger.info(String.format("uploading %s to %s", uploadTarget.getUploadFile().getName(), detectConfiguration.getProperty(DetectProperty.BLACKDUCK_URL, PropertyAuthority.None)));
            uploadBatch.addUploadTarget(uploadTarget);
        }

        BdioUploadCodeLocationCreationRequest uploadRequest = bdioUploadService.createUploadRequest(uploadBatch);
        return bdioUploadService.uploadBdio(uploadRequest);
    }

}
