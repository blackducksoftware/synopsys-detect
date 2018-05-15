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
import com.blackducksoftware.integration.hub.detect.codelocation.ScanCodeLocationNameProvider;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.detect.summary.Result;
import com.blackducksoftware.integration.hub.detect.summary.ScanSummaryResult;
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.service.SignatureScannerService;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.hub.service.model.ProjectVersionWrapper;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class HubSignatureScanner implements SummaryResultReporter, ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(HubSignatureScanner.class);
    private final Set<String> registeredPaths = new HashSet<>();
    private final Set<String> registeredPathsToExclude = new HashSet<>();
    private final Map<String, Result> scanSummaryResults = new HashMap<>();

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private OfflineScanner offlineScanner;

    @Autowired
    private ScanCodeLocationNameProvider scanCodeLocationNameProvider;

    public void registerPathToScan(final ScanPathSource scanPathSource, final File file, final String... fileNamesToExclude) throws IntegrationException {
        try {
            final boolean shouldRegisterPath = shouldRegisterPathForScanning(file, scanPathSource);

            if (shouldRegisterPath) {
                logger.info(String.format("Registering path %s to scan", file.getCanonicalPath()));
                scanSummaryResults.put(file.getCanonicalPath(), Result.FAILURE);
                registeredPaths.add(file.getCanonicalPath());
                if (null != fileNamesToExclude && fileNamesToExclude.length > 0) {
                    for (final String fileNameToExclude : fileNamesToExclude) {
                        final File fileToExclude = detectFileManager.findFile(file, fileNameToExclude);
                        if (null != fileToExclude) {
                            String pattern = fileToExclude.getCanonicalPath().replace(file.getCanonicalPath(), "");
                            if (pattern.contains("\\\\")) {
                                pattern = pattern.replace("\\\\", "/");
                            }
                            if (pattern.contains("\\")) {
                                pattern = pattern.replace("\\", "/");
                            }
                            pattern = pattern + "/";
                            registeredPathsToExclude.add(pattern);
                        }
                    }
                }
            }
        } catch (final IOException e) {
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    public ProjectVersionView scanPaths(final HubServerConfig hubServerConfig, final SignatureScannerService signatureScannerService, final DetectProject detectProject)
            throws IntegrationException, DetectUserFriendlyException, InterruptedException {
        ProjectVersionView projectVersionView = null;
        final ProjectRequest projectRequest = createProjectRequest(detectProject);
        Set<String> canonicalPathsToScan = registeredPaths;
        if (null != detectProject.getProjectName() && null != detectProject.getProjectVersionName() && null != detectConfiguration.getHubSignatureScannerPaths() && detectConfiguration.getHubSignatureScannerPaths().length > 0) {
            canonicalPathsToScan = new HashSet<>();
            for (final String path : detectConfiguration.getHubSignatureScannerPaths()) {
                try {
                    canonicalPathsToScan.add(new File(path).getCanonicalPath());
                } catch (final IOException e) {
                    throw new IntegrationException(e.getMessage(), e);
                }
            }
        }

        final List<ScanPathCallable> scanPathCallables = new ArrayList<>();
        for (final String canonicalPath : canonicalPathsToScan) {
            final HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, canonicalPath);
            final HubScanConfig hubScanConfig = hubScanConfigBuilder.build();
            final ScanPathCallable scanPathCallable = new ScanPathCallable(signatureScannerService, hubServerConfig, hubScanConfig, projectRequest, canonicalPath, scanSummaryResults);
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

    public void scanPathsOffline(final DetectProject detectProject) throws IOException {
        if (null != detectProject.getProjectName() && null != detectProject.getProjectVersionName() && null != detectConfiguration.getHubSignatureScannerPaths() && detectConfiguration.getHubSignatureScannerPaths().length > 0) {
            for (final String path : detectConfiguration.getHubSignatureScannerPaths()) {
                scanPathOffline(new File(path).getCanonicalPath(), detectProject);
            }
        } else {
            for (final String path : registeredPaths) {
                logger.info(String.format("Attempting to scan %s for %s/%s", path, detectProject.getProjectName(), detectProject.getProjectVersionName()));
                scanPathOffline(path, detectProject);
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

    private boolean shouldRegisterPathForScanning(final File file, final ScanPathSource scanPathSource) throws IOException {
        if (detectConfiguration.getHubSignatureScannerDisabled()) {
            logger.info(String.format("Not scanning path %s, the signature scanner is disabled.", file.getCanonicalPath()));
            return false;
        }

        final boolean customPathOverride = null != detectConfiguration.getHubSignatureScannerPaths() && detectConfiguration.getHubSignatureScannerPaths().length > 0;
        if (customPathOverride) {
            logger.info(String.format("Not scanning path %s, explicit scan paths were provided.", file.getCanonicalPath()));
            return false;
        }

        String matchingExcludedPath = null;
        for (final String pathToExclude : detectConfiguration.getHubSignatureScannerPathsToExclude()) {
            if (file.getCanonicalPath().startsWith(pathToExclude)) {
                matchingExcludedPath = pathToExclude;
            }
        }

        if (StringUtils.isNotBlank(matchingExcludedPath)) {
            logger.info(String.format("Not scanning path %s, it is excluded.", file.getCanonicalPath()));
            return false;
        }

        if (!file.exists() || (!file.isFile() && !file.isDirectory())) {
            logger.warn(String.format("Not scanning path %s, it doesn't appear to exist or it isn't a file or directory.", file.getCanonicalPath()));
            return false;
        }

        final boolean snippetModeEnabled = detectConfiguration.getHubSignatureScannerSnippetMode();
        final String sourcePath = detectConfiguration.getSourcePath();
        if (snippetModeEnabled && !(scanPathSource.equals(ScanPathSource.DOCKER_SOURCE) || scanPathSource.equals(ScanPathSource.DETECT_SOURCE) || scanPathSource.equals(ScanPathSource.SNIPPET_SOURCE))) {
            logger.info(String.format("Not scanning path %s, snippet mode is enabled and %s paths should be scanned when %s is scanned.", file.getCanonicalPath(), scanPathSource.getSource(), sourcePath));
            return false;
        }

        return true;
    }

    private void scanPathOffline(final String canonicalPath, final DetectProject detectProject) {
        try {
            final HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, canonicalPath);
            hubScanConfigBuilder.setDryRun(true);

            if (StringUtils.isBlank(detectConfiguration.getHubSignatureScannerOfflineLocalPath())) {
                final File toolsDirectory = detectFileManager.createDirectory("tools", false);
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

    private ProjectRequest createProjectRequest(final DetectProject detectProject) {
        final ProjectRequestBuilder builder = detectProject.createDefaultProjectRequestBuilder(detectConfiguration);
        return builder.build();
    }

    private HubScanConfigBuilder createScanConfigBuilder(final DetectProject detectProject, final String canonicalPath) {
        final File scannerDirectory = new File(detectConfiguration.getScanOutputDirectoryPath());
        final File toolsDirectory = detectFileManager.createDirectory("tools", false);

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
        final String codeLocationName = scanCodeLocationNameProvider.generateName(sourcePath, canonicalPath, projectName, projectVersionName, prefix, suffix);
        hubScanConfigBuilder.setCodeLocationAlias(codeLocationName);

        if (null != detectConfiguration.getHubSignatureScannerExclusionPatterns() && detectConfiguration.getHubSignatureScannerExclusionPatterns().length > 0) {
            hubScanConfigBuilder.setExcludePatterns(detectConfiguration.getHubSignatureScannerExclusionPatterns());
        } else if (null != registeredPathsToExclude && !registeredPathsToExclude.isEmpty()) {
            hubScanConfigBuilder.setExcludePatterns(registeredPathsToExclude.toArray(new String[registeredPathsToExclude.size()]));
        }

        return hubScanConfigBuilder;
    }

}
