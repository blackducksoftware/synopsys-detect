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
package com.blackducksoftware.integration.hub.detect.hub;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.dataservice.cli.CLIDataService;
import com.blackducksoftware.integration.hub.detect.summary.Result;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.model.request.ProjectRequest;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.scan.HubScanConfig;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;

public class ScanPathCallable implements Callable<ProjectVersionView> {
    private final Logger logger = LoggerFactory.getLogger(ScanPathCallable.class);

    private final CLIDataService cliDataService;
    private final HubServerConfig hubServerConfig;
    private final HubScanConfig hubScanConfig;
    private final ProjectRequest projectRequest;
    private final String canonicalPath;
    private final String hubDetectVersion;
    private final Map<String, Result> scanSummaryResults;

    public ScanPathCallable(final CLIDataService cliDataService, final HubServerConfig hubServerConfig, final HubScanConfig hubScanConfig, final ProjectRequest projectRequest, final String canonicalPath, final String hubDetectVersion,
            final Map<String, Result> scanSummaryResults) {
        this.cliDataService = cliDataService;
        this.hubServerConfig = hubServerConfig;
        this.hubScanConfig = hubScanConfig;
        this.projectRequest = projectRequest;
        this.canonicalPath = canonicalPath;
        this.hubDetectVersion = hubDetectVersion;
        this.scanSummaryResults = scanSummaryResults;
    }

    @Override
    public ProjectVersionView call() throws Exception {
        ProjectVersionView projectVersionView = null;
        try {
            logger.info(String.format("Attempting to scan %s for %s/%s", canonicalPath, projectRequest.getName(), projectRequest.getVersionRequest().getVersionName()));
            projectVersionView = cliDataService.installAndRunControlledScan(hubServerConfig, hubScanConfig, projectRequest, false, ThirdPartyName.DETECT, hubDetectVersion, hubDetectVersion);
            scanSummaryResults.put(canonicalPath, Result.SUCCESS);
            logger.info(String.format("%s was successfully scanned by the BlackDuck CLI.", canonicalPath));
        } catch (final Exception e) {
            logger.error(String.format("%s/%s - %s was not scanned by the BlackDuck CLI: %s", projectRequest.getName(), projectRequest.getVersionRequest().getVersionName(), canonicalPath, e.getMessage()));
        }
        return projectVersionView;
    }

}
