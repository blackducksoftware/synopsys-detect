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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.synopsys.integration.blackduck.api.generated.component.ProjectRequest;
import com.synopsys.integration.blackduck.api.generated.component.ProjectVersionRequest;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.api.view.ScanSummaryView;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.exception.DoesNotExistException;
import com.synopsys.integration.blackduck.exception.HubTimeoutExceededException;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.blackduck.service.HubService;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.ReportService;
import com.synopsys.integration.blackduck.service.ScanStatusService;
import com.synopsys.integration.blackduck.service.model.PolicyStatusDescription;
import com.synopsys.integration.blackduck.service.model.ProjectRequestBuilder;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.exception.IntegrationRestException;

public class HubManager implements ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(HubManager.class);

    private final BlackDuckBinaryScanner blackDuckBinaryScanner;
    private final BdioUploader bdioUploader;
    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;
    private final HubServiceManager hubServiceManager;
    private final BlackDuckSignatureScanner blackDuckSignatureScanner;
    private final PolicyChecker policyChecker;

    private ExitCodeType exitCodeType = ExitCodeType.SUCCESS;

    public HubManager(final BdioUploader bdioUploader, final CodeLocationNameManager codeLocationNameManager, final DetectConfiguration detectConfiguration, final HubServiceManager hubServiceManager,
            final BlackDuckSignatureScanner blackDuckSignatureScanner, final PolicyChecker policyChecker, final BlackDuckBinaryScanner blackDuckBinaryScanner) {
        this.bdioUploader = bdioUploader;
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
        this.hubServiceManager = hubServiceManager;
        this.blackDuckSignatureScanner = blackDuckSignatureScanner;
        this.policyChecker = policyChecker;
        this.blackDuckBinaryScanner = blackDuckBinaryScanner;
    }

    public Optional<ProjectVersionView> updateHubProjectVersion(final DetectProject detectProject) throws IntegrationException, DetectUserFriendlyException, InterruptedException {
        final ProjectService projectService = hubServiceManager.createProjectService();
        final HubService hubService = hubServiceManager.createHubService();
        final ProjectVersionView projectVersionView = ensureProjectVersionExists(detectProject, projectService, hubService);
        if (null != detectProject.getBdioFiles() && !detectProject.getBdioFiles().isEmpty()) {
            final CodeLocationService codeLocationService = hubServiceManager.createCodeLocationService();
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_UNMAP)) {
                try {
                    final List<CodeLocationView> codeLocationViews = hubService.getAllResponses(projectVersionView, ProjectVersionView.CODELOCATIONS_LINK_RESPONSE);

                    for (final CodeLocationView codeLocationView : codeLocationViews) {
                        codeLocationService.unmapCodeLocation(codeLocationView);
                    }
                } catch (final IntegrationException e) {
                    throw new DetectUserFriendlyException(String.format("There was a problem unmapping Code Locations: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
                }
            }
            bdioUploader.uploadBdioFiles(codeLocationService, detectProject);
        } else {
            logger.debug("Did not create any bdio files.");
        }

        return Optional.ofNullable(projectVersionView);
    }

    public void performScanActions(final DetectProject detectProject) throws IntegrationException, InterruptedException, DetectUserFriendlyException {
        if (!detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED)) {
            final HubServerConfig hubServerConfig = hubServiceManager.getHubServerConfig();
            final ExecutorService executorService = Executors.newFixedThreadPool(detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS));
            try {
                final ScanJobManager scanJobManager = hubServiceManager.createScanJobManager(executorService);
                blackDuckSignatureScanner.scanPaths(hubServerConfig, scanJobManager, detectProject);
            } finally {
                executorService.shutdownNow();
            }
        }
    }

    public void performBinaryScanActions(final DetectProject detectProject) throws DetectUserFriendlyException {
        if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE))) {
            final String prefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX);
            final String suffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX);

            final File file = new File(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE));
            blackDuckBinaryScanner.uploadBinaryScanFile(hubServiceManager.createBinaryScannerService(), file, detectProject.getProjectName(), detectProject.getProjectVersion(), prefix, suffix);
        } else {
            logger.debug("No binary scan path was provided, so binary scan will not occur.");
        }
    }

    public void performPostHubActions(final DetectProject detectProject, final ProjectVersionView projectVersionView) throws DetectUserFriendlyException {
        try {
            final ProjectService projectService = hubServiceManager.createProjectService();
            final ReportService reportService = hubServiceManager.createReportService();
            final HubService hubService = hubServiceManager.createHubService();
            final CodeLocationService codeLocationService = hubServiceManager.createCodeLocationService();
            final ScanStatusService scanStatusService = hubServiceManager.createScanStatusService();

            if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES)) || detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF)
                    || detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT)) {
                waitForBomUpdate(codeLocationService, hubService, scanStatusService);
            }

            if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES))) {
                final PolicyStatusDescription policyStatusDescription = policyChecker.getPolicyStatus(projectService, projectVersionView);
                logger.info(policyStatusDescription.getPolicyStatusMessage());
                if (policyChecker.policyViolated(policyStatusDescription)) {
                    exitCodeType = ExitCodeType.FAILURE_POLICY_VIOLATION;

                }
            }

            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF)) {
                logger.info("Creating risk report pdf");
                final File pdfFile = reportService.createReportPdfFile(new File(detectConfiguration.getProperty(DetectProperty.DETECT_RISK_REPORT_PDF_PATH)), detectProject.getProjectName(), detectProject.getProjectVersion());
                logger.info(String.format("Created risk report pdf: %s", pdfFile.getCanonicalPath()));
            }

            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT)) {
                logger.info("Creating notices report");
                final File noticesFile = reportService.createNoticesReportFile(new File(detectConfiguration.getProperty(DetectProperty.DETECT_NOTICES_REPORT_PATH)), detectProject.getProjectName(), detectProject.getProjectVersion());
                if (noticesFile != null) {
                    logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));
                }
            }

            if (!detectProject.getBdioFiles().isEmpty() || !detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED)) {
                // only log BOM URL if we have updated it in some way
                final ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersion(detectProject.getProjectName(), detectProject.getProjectVersion());
                final String componentsLink = hubService.getFirstLinkSafely(projectVersionWrapper.getProjectVersionView(), ProjectVersionView.COMPONENTS_LINK);
                logger.info(String.format("To see your results, follow the URL: %s", componentsLink));
            }
        } catch (final IllegalStateException e) {
            throw new DetectUserFriendlyException(String.format("Your Hub configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        } catch (final IntegrationRestException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        } catch (final HubTimeoutExceededException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_TIMEOUT);
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("There was a problem: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    @Override
    public ExitCodeType getExitCodeType() {
        return exitCodeType;
    }

    private void waitForBomUpdate(final CodeLocationService codeLocationService, final HubService hubService, final ScanStatusService scanStatusService) throws IntegrationException, InterruptedException {
        final List<CodeLocationView> allCodeLocations = new ArrayList<>();
        for (final String codeLocationName : codeLocationNameManager.getCodeLocationNames()) {
            final CodeLocationView codeLocationView = codeLocationService.getCodeLocationByName(codeLocationName);
            allCodeLocations.add(codeLocationView);
        }
        final List<ScanSummaryView> scanSummaryViews = new ArrayList<>();
        for (final CodeLocationView codeLocationView : allCodeLocations) {
            final String scansLink = hubService.getFirstLinkSafely(codeLocationView, CodeLocationView.SCANS_LINK);
            if (StringUtils.isNotBlank(scansLink)) {
                final List<ScanSummaryView> codeLocationScanSummaryViews = hubService.getResponses(scansLink, ScanSummaryView.class, true);
                scanSummaryViews.addAll(codeLocationScanSummaryViews);
            }
        }
        logger.info("Waiting for the BOM to be updated");
        scanStatusService.assertScansFinished(scanSummaryViews);
        logger.info("The BOM has been updated");
    }

    public ProjectVersionView ensureProjectVersionExists(final DetectProject detectProject, final ProjectService projectService, final HubService hubService) throws IntegrationException, DetectUserFriendlyException {
        final ProjectRequest projectRequest = createProjectRequest(detectProject, projectService, hubService);

        final boolean forceUpdate = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_VERSION_UPDATE);

        return getProjectVersionAndUpdateOrCreateIfNeeded(projectRequest, projectService, hubService, forceUpdate);
    }

    private ProjectVersionView getProjectVersionAndUpdateOrCreateIfNeeded(final ProjectRequest projectRequest, final ProjectService projectService, final HubService hubService, final boolean forceUpdate) throws IntegrationException {
        ProjectView project = null;
        ProjectVersionView projectVersion = null;
        boolean shouldUpdateProject = true;
        try {
            logger.debug("Checking for project.");
            project = projectService.getProjectByName(projectRequest.name);
        } catch (final DoesNotExistException e) {
            logger.debug("Creating project.");
            final String projectURL = projectService.createHubProject(projectRequest);
            project = hubService.getResponse(projectURL, ProjectView.class);
            shouldUpdateProject = false;
        }

        if (forceUpdate && shouldUpdateProject) {
            logger.debug("Updating project.");
            final ProjectVersionRequest cachedRequest = projectRequest.versionRequest;
            projectRequest.versionRequest = null;
            projectService.updateProjectAndVersion(project, projectRequest);
            projectRequest.versionRequest = cachedRequest;
        }

        boolean shouldUpdateVersion = true;
        try {
            logger.debug("Checking for version.");
            projectVersion = projectService.getProjectVersion(project, projectRequest.versionRequest.versionName);
        } catch (final DoesNotExistException e) {
            logger.debug("Creating version.");
            final String versionURL = projectService.createHubVersion(project, projectRequest.versionRequest);
            projectVersion = hubService.getResponse(versionURL, ProjectVersionView.class);
            shouldUpdateVersion = false;
        }

        if (forceUpdate && shouldUpdateVersion) {
            logger.debug("Updating version.");
            projectService.updateProjectAndVersion(project, projectRequest);
        }

        return projectVersion;
    }

    public ProjectRequest createProjectRequest(final DetectProject detectProject, final ProjectService projectService, final HubService hubService) throws DetectUserFriendlyException {
        final ProjectRequestBuilder projectRequestBuilder = new ProjectRequestBuilder();

        projectRequestBuilder.setProjectName(detectProject.getProjectName());
        projectRequestBuilder.setVersionName(detectProject.getProjectVersion());

        projectRequestBuilder.setProjectLevelAdjustments(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_LEVEL_ADJUSTMENTS));
        projectRequestBuilder.setPhase(detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_PHASE));
        projectRequestBuilder.setDistribution(detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_DISTRIBUTION));
        projectRequestBuilder.setProjectTier(detectConfiguration.getIntegerProperty(DetectProperty.DETECT_PROJECT_TIER));
        projectRequestBuilder.setDescription(detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_DESCRIPTION));
        projectRequestBuilder.setReleaseComments(detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NOTES));
        projectRequestBuilder.setCloneCategories(convertClonePropertyToEnum(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_PROJECT_CLONE_CATEGORIES)));

        final Optional<String> cloneUrl = findCloneUrl(detectProject, projectService, hubService);
        if (cloneUrl.isPresent()) {
            logger.info("Cloning project version from release url: " + cloneUrl.get());
            projectRequestBuilder.setCloneFromReleaseUrl(cloneUrl.get());
        }

        return projectRequestBuilder.build();
    }

    private List<ProjectCloneCategoriesType> convertClonePropertyToEnum(final String[] cloneCategories) {
        final List<ProjectCloneCategoriesType> categories = new ArrayList<>();
        for (final String category : cloneCategories) {
            categories.add(ProjectCloneCategoriesType.valueOf(category));
        }
        logger.debug("Found clone categories:" + categories.stream().map(it -> it.toString()).collect(Collectors.joining(",")));
        return categories;
    }

    public Optional<String> findCloneUrl(final DetectProject detectProject, final ProjectService projectService, final HubService hubService) throws DetectUserFriendlyException {
        final String cloneProjectName = detectProject.getProjectName();
        final String cloneProjectVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_CLONE_PROJECT_VERSION_NAME);
        if (StringUtils.isBlank(cloneProjectName) || StringUtils.isBlank(cloneProjectVersionName)) {
            logger.debug("No clone project or version name supplied. Will not clone.");
            return Optional.empty();
        }
        try {
            final ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersion(cloneProjectName, cloneProjectVersionName);
            final String url = hubService.getHref(projectVersionWrapper.getProjectVersionView());
            return Optional.of(url);
        } catch (final IntegrationException e) {
            throw new DetectUserFriendlyException("Unable to find clone release url for supplied clone version name.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }

    }

}
