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
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameService;
import com.synopsys.integration.blackduck.service.BinaryScannerService;
import com.synopsys.integration.exception.IntegrationException;

public class BlackDuckBinaryScanner {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckBinaryScanner.class);

    private final CodeLocationNameService codeLocationNameService;

    public BlackDuckBinaryScanner(final CodeLocationNameService codeLocationNameService) {
        this.codeLocationNameService = codeLocationNameService;
    }

    public void uploadBinaryScanFile(final BinaryScannerService binaryService, final File file, final String projectName, final String projectVersionName, final String prefix, final String suffix) throws DetectUserFriendlyException {
        final String codeLocationName = codeLocationNameService.createBinaryScanCodeLocationName(file.getName(), projectName, projectVersionName, prefix, suffix);
        try {
            logger.info("Preparing to upload binary scan file: " + codeLocationName);
            binaryService.scanBinary(file, projectName, projectVersionName, codeLocationName);
            logger.info("Succesfully uploaded binary scan file: " + codeLocationName);
        } catch (MalformedURLException | IntegrationException | URISyntaxException e) {
            throw new DetectUserFriendlyException("Failed to upload binary scan file.", e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        }
    }

}
