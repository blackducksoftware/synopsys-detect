/**
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.binaryscanner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.service.BinaryScannerService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckBinaryScannerTool {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckBinaryScannerTool.class);

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

    public BinaryScanToolResult performBinaryScanActions(final NameVersion projectNameVersion) throws DetectUserFriendlyException {
        if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None))) {
            final String prefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX, PropertyAuthority.None);
            final String suffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX, PropertyAuthority.None);

            final File file = new File(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None));

            NotificationTaskRange taskRange = calculateTaskRange();
            Set<String> codeLocationNames = uploadBinaryScanFile(blackDuckServicesFactory.createBinaryScannerService(), file, projectNameVersion.getName(), projectNameVersion.getVersion(), prefix, suffix);

            return new BinaryScanToolResult(taskRange, codeLocationNames, true);
        } else {
            logger.debug("No binary scan path was provided, so binary scan will not occur.");
            return new BinaryScanToolResult(null, null, false);
        }
    }

    public NotificationTaskRange calculateTaskRange()  throws DetectUserFriendlyException {
        CodeLocationCreationService codeLocationCreationService = blackDuckServicesFactory.createCodeLocationCreationService();
        try {
            return codeLocationCreationService.calculateCodeLocationRange();
        } catch (IntegrationException e) {
            throw new DetectUserFriendlyException("Failed to calculate binary scan notification range", e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
        }
    }
    public Set<String> uploadBinaryScanFile(final BinaryScannerService binaryService, final File file, final String projectName, final String projectVersionName, final String prefix, final String suffix) throws DetectUserFriendlyException {
        final String codeLocationName = codeLocationNameManager.createBinaryScanCodeLocationName(file.getName(), projectName, projectVersionName, prefix, suffix);
        try {
            logger.info("Preparing to upload binary scan file: " + codeLocationName);
            binaryService.scanBinary(file, projectName, projectVersionName, codeLocationName);
            logger.info("Successfully uploaded binary scan file: " + codeLocationName);
            eventSystem.publishEvent(Event.StatusSummary, new Status("BINARY_SCAN", StatusType.SUCCESS));
            Set<String> names = new HashSet<String>();
            names.add(codeLocationName);
            return  names;
        } catch (IOException | IntegrationException | URISyntaxException e) {
            logger.error("Failed to upload binary scan file: " + e.getMessage());
            eventSystem.publishEvent(Event.StatusSummary, new Status("BINARY_SCAN", StatusType.FAILURE));
            throw new DetectUserFriendlyException("Failed to upload binary scan file.", e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        }
    }

}
