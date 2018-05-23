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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.configuration.HubScanConfig;
import com.blackducksoftware.integration.hub.configuration.HubScanConfigBuilder;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.codelocation.ScanCodeLocationNameFactory;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.detect.summary.Result;
import com.blackducksoftware.integration.hub.detect.summary.ScanSummaryResult;
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.service.SignatureScannerService;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.hub.service.model.ProjectVersionWrapper;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class HubSignatureScanner implements SummaryResultReporter, ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(HubSignatureScanner.class);
    private final Set<String> scanPaths = new HashSet<>();
    private final Map<String, Set<String>> scanPathExclusionPatterns = new HashMap<>();
    private final Map<String, Result> scanSummaryResults = new HashMap<>();

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private DetectFileFinder detectFileFinder;

    @Autowired
    private OfflineScanner offlineScanner;

    @Autowired
    private ScanCodeLocationNameFactory scanCodeLocationNameFactory;

    public ProjectVersionView scanPaths(final HubServerConfig hubServerConfig, final SignatureScannerService signatureScannerService, final DetectProject detectProject)
            throws IntegrationException, DetectUserFriendlyException, InterruptedException {
        determinePathsAndExclusions(detectProject);

        ProjectVersionView projectVersionView = null;
        final ProjectRequest projectRequest = createProjectRequest(detectProject);

        final List<ScanPathCallable> scanPathCallables = new ArrayList<>();
        for (final String scanPath : scanPaths) {
            final HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, scanPath, scanPathExclusionPatterns.get(scanPath));
            final HubScanConfig hubScanConfig = hubScanConfigBuilder.build();
            final ScanPathCallable scanPathCallable = new ScanPathCallable(signatureScannerService, hubServerConfig, hubScanConfig, projectRequest, scanPath, scanSummaryResults);
            scanPathCallables.add(scanPathCallable);
        }

        final ExecutorService pool = Executors.newFixedThreadPool(detectConfiguration.getHubSignatureScannerParallelProcessors());
        try {
            final List<Future<ProjectVersionWrapper>> submittedScanPathCallables = new ArrayList<>();
            for (final ScanPathCallable scanPathCallable : scanPathCallables) {
                submittedScanPathCallables.add(pool.submit(scanPathCallable));
            }
            for (final Future<ProjectVersionWrapper> futureProjectVersionWrapper : submittedScanPathCallables) {
                final ProjectVersionWrapper projectVersionWrapperFromScan = futureProjectVersionWrapper.get();
                if (projectVersionWrapperFromScan != null) {
                    projectVersionView = projectVersionWrapperFromScan.getProjectVersionView();
                }
            }
        } catch (final ExecutionException e) {
            throw new DetectUserFriendlyException(String.format("Encountered a problem waiting for a scan to finish. %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } finally {
            // get() was called on every java.util.concurrent.Future, no need to wait any longer
            pool.shutdownNow();
        }

        return projectVersionView;
    }

    public void scanPathsOffline(final DetectProject detectProject) throws IOException, IntegrationException {
        determinePathsAndExclusions(detectProject);
        if (null != detectProject.getProjectName() && null != detectProject.getProjectVersionName() && null != detectConfiguration.getHubSignatureScannerPaths() && detectConfiguration.getHubSignatureScannerPaths().length > 0) {
            for (final String path : detectConfiguration.getHubSignatureScannerPaths()) {
                scanPathOffline(new File(path).getCanonicalPath(), detectProject);
            }
        } else {
            for (final String scanPath : scanPaths) {
                logger.info(String.format("Attempting to scan %s for %s/%s", scanPath, detectProject.getProjectName(), detectProject.getProjectVersionName()));
                scanPathOffline(scanPath, detectProject);
            }
        }
    }

    @Override
    public List<ScanSummaryResult> getDetectSummaryResults() {
        final List<ScanSummaryResult> detectSummaryResults = new ArrayList<>();
        for (final Map.Entry<String, Result> entry : scanSummaryResults.entrySet()) {
            detectSummaryResults.add(new ScanSummaryResult(entry.getKey(), entry.getValue()));
        }
        return detectSummaryResults;
    }

    @Override
    public ExitCodeType getExitCodeType() {
        for (final Map.Entry<String, Result> entry : scanSummaryResults.entrySet()) {
            if (Result.FAILURE == entry.getValue()) {
                return ExitCodeType.FAILURE_SCAN;
            }
        }
        return ExitCodeType.SUCCESS;
    }

    private void scanPathOffline(final String canonicalPath, final DetectProject detectProject) {
        try {
            final HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, canonicalPath, scanPathExclusionPatterns.get(canonicalPath));
            hubScanConfigBuilder.setDryRun(true);

            if (StringUtils.isBlank(detectConfiguration.getHubSignatureScannerOfflineLocalPath())) {
                final File toolsDirectory = detectFileManager.getPermanentDirectory();
                hubScanConfigBuilder.setToolsDir(toolsDirectory);
            }

            final HubScanConfig hubScanConfig = hubScanConfigBuilder.build();
            final boolean pathWasScanned = offlineScanner.offlineScan(detectProject, hubScanConfig, detectConfiguration.getHubSignatureScannerOfflineLocalPath());
            if (pathWasScanned) {
                scanSummaryResults.put(canonicalPath, Result.SUCCESS);
                logger.info(String.format("%s was successfully scanned by the BlackDuck CLI.", canonicalPath));
            }
        } catch (final Exception e) {
            logger.error(String.format("%s/%s - %s was not scanned by the BlackDuck CLI: %s", detectProject.getProjectName(), detectProject.getProjectVersionName(), canonicalPath, e.getMessage()));
        }
    }

    private void determinePathsAndExclusions(final DetectProject detectProject) throws IntegrationException {
        boolean userProvidedScanTargets = null != detectConfiguration.getHubSignatureScannerPaths() && detectConfiguration.getHubSignatureScannerPaths().length > 0;
        String[] userProvidedExclusionPatterns = detectConfiguration.getHubSignatureScannerExclusionPatterns();
        String[] hubSignatureScannerExclusionNamePatterns = detectConfiguration.getHubSignatureScannerExclusionNamePatterns();
        if (null != detectProject.getProjectName() && null != detectProject.getProjectVersionName() && userProvidedScanTargets) {
            for (final String path : detectConfiguration.getHubSignatureScannerPaths()) {
                logger.info(String.format("Registering explicit scan path %s", path));
                addScanTarget(path, hubSignatureScannerExclusionNamePatterns, userProvidedExclusionPatterns);
            }
        } else {
            String sourcePath = detectConfiguration.getSourcePath();
            if (userProvidedScanTargets) {
                logger.warn(String.format("No Project name or version found. Skipping User provided scan targets - registering the source path %s to scan", sourcePath));
            } else {
                logger.info(String.format("No scan targets provided - registering the source path %s to scan", sourcePath));
            }
            addScanTarget(sourcePath, hubSignatureScannerExclusionNamePatterns, userProvidedExclusionPatterns);
        }
    }

    private void addScanTarget(String path, String[] hubSignatureScannerExclusionNamePatterns, String[] userProvidedExclusionPatterns) throws IntegrationException {
        try {
            File target = new File(path);
            String targetPath = target.getCanonicalPath();
            scanPaths.add(targetPath);
            ExclusionPatternDetector exclusionPatternDetector = new ExclusionPatternDetector(detectFileFinder, target);
            Set<String> scanExclusionPatterns = exclusionPatternDetector.determineExclusionPatterns(hubSignatureScannerExclusionNamePatterns);
            if (null != userProvidedExclusionPatterns) {
                for (String userProvidedExclusionPattern : userProvidedExclusionPatterns) {
                    scanExclusionPatterns.add(userProvidedExclusionPattern);
                }
            }
            scanPathExclusionPatterns.put(targetPath, scanExclusionPatterns);
        } catch (final IOException e) {
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    private ProjectRequest createProjectRequest(final DetectProject detectProject) {
        final ProjectRequestBuilder builder = detectProject.createDefaultProjectRequestBuilder(detectConfiguration);
        return builder.build();
    }

    private HubScanConfigBuilder createScanConfigBuilder(final DetectProject detectProject, final String canonicalPath, Set<String> exclusionPatterns) {
        final File scannerDirectory = new File(detectConfiguration.getScanOutputDirectoryPath());
        final File toolsDirectory = detectFileManager.getPermanentDirectory();

        final HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder();
        hubScanConfigBuilder.setScanMemory(detectConfiguration.getHubSignatureScannerMemory());
        hubScanConfigBuilder.setToolsDir(toolsDirectory);
        hubScanConfigBuilder.setWorkingDirectory(scannerDirectory);
        hubScanConfigBuilder.addScanTargetPath(canonicalPath);
        hubScanConfigBuilder.setCleanupLogsOnSuccess(detectConfiguration.getCleanupDetectFiles());
        hubScanConfigBuilder.setDryRun(detectConfiguration.getHubSignatureScannerDryRun());
        hubScanConfigBuilder.setSnippetModeEnabled(detectConfiguration.getHubSignatureScannerSnippetMode());

        final String projectName = detectProject.getProjectName();
        final String projectVersionName = detectProject.getProjectVersionName();
        final String sourcePath = detectConfiguration.getSourcePath();
        final String prefix = detectConfiguration.getProjectCodeLocationPrefix();
        final String suffix = detectConfiguration.getProjectCodeLocationSuffix();
        final String codeLocationName = scanCodeLocationNameFactory.createCodeLocationName(sourcePath, canonicalPath, projectName, projectVersionName, prefix, suffix);
        hubScanConfigBuilder.setCodeLocationAlias(codeLocationName);

        if (null != exclusionPatterns && !exclusionPatterns.isEmpty()) {
            hubScanConfigBuilder.setExcludePatterns(exclusionPatterns.toArray(new String[exclusionPatterns.size()]));
        }

        return hubScanConfigBuilder;
    }

}
