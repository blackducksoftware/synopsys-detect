/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.FileManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.exception.IntegrationException;

public class BdioUploader {
    private final Logger logger = LoggerFactory.getLogger(BdioUploader.class);

    private final DetectConfiguration detectConfiguration;
    private final FileManager fileManager;

    public BdioUploader(final DetectConfiguration detectConfiguration, final FileManager fileManager) {
        this.detectConfiguration = detectConfiguration;
        this.fileManager = fileManager;
    }

    public void uploadBdioFiles(final CodeLocationService codeLocationService, final DetectProject detectProject) throws IntegrationException {
        for (final File file : detectProject.getBdioFiles()) {
            logger.info(String.format("uploading %s to %s", file.getName(), detectConfiguration.getProperty(DetectProperty.BLACKDUCK_URL, PropertyAuthority.None)));
            codeLocationService.importBomFile(file);
            fileManager.registerOutputFileForCleanup(file);
        }
    }

}
