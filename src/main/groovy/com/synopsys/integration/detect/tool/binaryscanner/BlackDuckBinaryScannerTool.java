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
package com.synopsys.integration.detect.tool.binaryscanner;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScan;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatch;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanUploadService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class BlackDuckBinaryScannerTool {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckBinaryScannerTool.class);
    private static final String STATUS_KEY = "BINARY_SCAN";

    private final CodeLocationNameManager codeLocationNameManager;
    private DetectConfiguration detectConfiguration;
    private BlackDuckServicesFactory blackDuckServicesFactory;
    private EventSystem eventSystem;

    public BlackDuckBinaryScannerTool(EventSystem eventSystem, final CodeLocationNameManager codeLocationNameManager, final DetectConfiguration detectConfiguration, final BlackDuckServicesFactory blackDuckServicesFactory) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.eventSystem = eventSystem;
    }

    public boolean shouldRun() {
        final String binaryScanFilePath = getBinaryScanFilePath();
        if (StringUtils.isBlank(binaryScanFilePath)) {
            logger.info("No binary scan file path provided; binary scan will not run");
            return false;
        }
        return true;
    }

    public BinaryScanToolResult performBinaryScanActions(final NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final String binaryScanFilePath = getBinaryScanFilePath();
        final File binaryScanFile = new File(binaryScanFilePath);
        if (binaryScanFile.isFile() && binaryScanFile.canRead()) {
            final String prefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX, PropertyAuthority.None);
            final String suffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX, PropertyAuthority.None);
            final CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = uploadBinaryScanFile(blackDuckServicesFactory.createBinaryScanUploadService(), binaryScanFile, projectNameVersion.getName(), projectNameVersion.getVersion(), prefix, suffix);
            return BinaryScanToolResult.SUCCESS(codeLocationCreationData);
        } else {
            logger.error("The binary scan file path does not point to a readable file.");
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.FAILURE));
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR, STATUS_KEY));
            return BinaryScanToolResult.FAILURE();
        }
    }

    public CodeLocationCreationData<BinaryScanBatchOutput> uploadBinaryScanFile(BinaryScanUploadService binaryScanUploadService, final File binaryScanFile, final String projectName, final String projectVersionName, final String prefix, final String suffix) throws DetectUserFriendlyException {
        final String codeLocationName = codeLocationNameManager.createBinaryScanCodeLocationName(binaryScanFile.getName(), projectName, projectVersionName, prefix, suffix);
        try {
            logger.info("Preparing to upload binary scan file: " + codeLocationName);
            BinaryScan binaryScan = new BinaryScan(binaryScanFile, projectName, projectVersionName, codeLocationName);
            BinaryScanBatch binaryScanBatch = new BinaryScanBatch(binaryScan);
            CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = binaryScanUploadService.uploadBinaryScan(binaryScanBatch);
            logger.info("Successfully uploaded binary scan file: " + codeLocationName);
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.SUCCESS));
            return codeLocationCreationData;
        } catch (IntegrationException e) {
            logger.error("Failed to upload binary scan file: " + e.getMessage());
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.FAILURE));
            throw new DetectUserFriendlyException("Failed to upload binary scan file.", e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        }
    }

    private String getBinaryScanFilePath() {
        return detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None);
    }

}
