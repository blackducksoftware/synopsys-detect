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
package com.blackducksoftware.integration.hub.detect.manager.hub;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest;
import com.blackducksoftware.integration.hub.configuration.HubScanConfig;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.detect.manager.result.summary.SummaryStatus;
import com.blackducksoftware.integration.hub.service.SignatureScannerService;
import com.blackducksoftware.integration.hub.service.model.ProjectVersionWrapper;

public class ScanPathCallable implements Callable<ProjectVersionWrapper> {
    private final Logger logger = LoggerFactory.getLogger(ScanPathCallable.class);

    private final SignatureScannerService signatureScannerService;
    private final HubServerConfig hubServerConfig;
    private final HubScanConfig hubScanConfig;
    private final ProjectRequest projectRequest;
    private final String canonicalPath;
    private final Map<String, SummaryStatus> scanSummaryResults;

    public ScanPathCallable(final SignatureScannerService signatureScannerService, final HubServerConfig hubServerConfig, final HubScanConfig hubScanConfig, final ProjectRequest projectRequest, final String canonicalPath,
            final Map<String, SummaryStatus> scanSummaryResults) {
        this.signatureScannerService = signatureScannerService;
        this.hubServerConfig = hubServerConfig;
        this.hubScanConfig = hubScanConfig;
        this.projectRequest = projectRequest;
        this.canonicalPath = canonicalPath;
        this.scanSummaryResults = scanSummaryResults;
    }

    @Override
    public ProjectVersionWrapper call() {
        ProjectVersionWrapper projectVersionWrapper = null;
        try {
            logger.info(String.format("Attempting to scan %s for %s/%s", canonicalPath, projectRequest.name, projectRequest.versionRequest.versionName));
            projectVersionWrapper = signatureScannerService.installAndRunControlledScan(hubServerConfig, hubScanConfig, projectRequest, false);
            scanSummaryResults.put(canonicalPath, SummaryStatus.SUCCESS);
            logger.info(String.format("%s was successfully scanned by the BlackDuck CLI.", canonicalPath));
        } catch (final Exception e) {
            logger.error(String.format("%s/%s - %s was not scanned by the BlackDuck CLI: %s", projectRequest.name, projectRequest.versionRequest.versionName, canonicalPath, e.getMessage()));
            return null;
        }
        return projectVersionWrapper;
    }

}
