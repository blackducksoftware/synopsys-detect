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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest;
import com.blackducksoftware.integration.hub.api.generated.view.CodeLocationView;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.api.view.ScanSummaryView;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceWrapper;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.blackducksoftware.integration.hub.exception.HubTimeoutExceededException;
import com.blackducksoftware.integration.hub.service.BinaryScannerService;
import com.blackducksoftware.integration.hub.service.CodeLocationService;
import com.blackducksoftware.integration.hub.service.HubService;
import com.blackducksoftware.integration.hub.service.ProjectService;
import com.blackducksoftware.integration.hub.service.ReportService;
import com.blackducksoftware.integration.hub.service.ScanStatusService;
import com.blackducksoftware.integration.hub.service.SignatureScannerService;
import com.blackducksoftware.integration.hub.service.model.PolicyStatusDescription;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.hub.service.model.ProjectVersionWrapper;
import com.blackducksoftware.integration.rest.exception.IntegrationRestException;

public class HubManager implements ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(HubManager.class);

    private final BdioUploader bdioUploader;
    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;
    private final HubServiceWrapper hubServiceWrapper;
    private final HubSignatureScanner hubSignatureScanner;
    private final PolicyChecker policyChecker;

    private ExitCodeType exitCodeType = ExitCodeType.SUCCESS;

    public HubManager(final BdioUploader bdioUploader, final CodeLocationNameManager codeLocationNameManager, final DetectConfiguration detectConfiguration, final HubServiceWrapper hubServiceWrapper,
            final HubSignatureScanner hubSignatureScanner, final PolicyChecker policyChecker) {
        this.bdioUploader = bdioUploader;
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
        this.hubServiceWrapper = hubServiceWrapper;
        this.hubSignatureScanner = hubSignatureScanner;
        this.policyChecker = policyChecker;
    }

    public ProjectVersionView updateHubProjectVersion(final DetectProject detectProject) throws IntegrationException, DetectUserFriendlyException, InterruptedException {
        final ProjectService projectService = hubServiceWrapper.createProjectService();
        ProjectVersionView projectVersionView = ensureProjectVersionExists(detectProject, projectService);
        if (null != detectProject.getBdioFiles() && !detectProject.getBdioFiles().isEmpty()) {
            final CodeLocationService codeLocationService = hubServiceWrapper.createCodeLocationService();
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_UNMAP)) {
                try {
                    final HubService hubService = hubServiceWrapper.createHubService();
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

        if (!detectConfiguration.getBooleanProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_DISABLED)) {
            final HubServerConfig hubServerConfig = hubServiceWrapper.getHubServerConfig();
            final ExecutorService executorService = Executors.newFixedThreadPool(detectConfiguration.getIntegerProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS));
            try {
                final SignatureScannerService signatureScannerService = hubServiceWrapper.createSignatureScannerService(executorService);
                final ProjectVersionView scanProject = hubSignatureScanner.scanPaths(hubServerConfig, signatureScannerService, detectProject);
                if (null == projectVersionView) {
                    projectVersionView = scanProject;
                }
            } finally {
                executorService.shutdownNow();
            }
        }

        final String binFilePath = detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE);
        if (StringUtils.isNotBlank(binFilePath)) {
            final File file = new File(binFilePath);
            if (file.exists()) {
                final BinaryScannerService binaryScannerService = hubServiceWrapper.createBinaryScannerService();
                try {
                    binaryScannerService.scanBinary(new File(binFilePath), detectProject.getProjectName(), detectProject.getProjectVersion(), file.getName());
                } catch (final MalformedURLException e) {
                    throw new DetectUserFriendlyException("An error occured uploading binary scan file.", ExitCodeType.FAILURE_CONFIGURATION);
                } catch (final URISyntaxException e) {
                    throw new DetectUserFriendlyException("An error occured uploading binary scan file.", ExitCodeType.FAILURE_CONFIGURATION);
                }
            }
        }

        return projectVersionView;
    }

    public void performOfflineHubActions(final DetectProject detectProject) throws IntegrationException {
        if (!detectConfiguration.getBooleanProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_DISABLED)) {
            hubSignatureScanner.scanPathsOffline(detectProject);
        }
    }

    public void performPostHubActions(final DetectProject detectProject, final ProjectVersionView projectVersionView) throws DetectUserFriendlyException {
        try {
            if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES)) || detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF) || detectConfiguration
                    .getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT)) {
                final ProjectService projectService = hubServiceWrapper.createProjectService();
                final ScanStatusService scanStatusService = hubServiceWrapper.createScanStatusService();

                waitForBomUpdate(hubServiceWrapper.createCodeLocationService(), hubServiceWrapper.createHubService(), scanStatusService);

                if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES))) {
                    final PolicyStatusDescription policyStatusDescription = policyChecker.getPolicyStatus(projectService, projectVersionView);
                    logger.info(policyStatusDescription.getPolicyStatusMessage());
                    if (policyChecker.policyViolated(policyStatusDescription)) {
                        exitCodeType = ExitCodeType.FAILURE_POLICY_VIOLATION;
                    }
                }

                if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF)) {
                    final ReportService reportService = hubServiceWrapper.createReportService();
                    logger.info("Creating risk report pdf");
                    final File pdfFile = reportService.createReportPdfFile(new File(detectConfiguration.getProperty(DetectProperty.DETECT_RISK_REPORT_PDF_PATH)), detectProject.getProjectName(), detectProject.getProjectVersion());
                    logger.info(String.format("Created risk report pdf: %s", pdfFile.getCanonicalPath()));
                }

                if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT)) {
                    final ReportService reportService = hubServiceWrapper.createReportService();
                    logger.info("Creating notices report");
                    final File noticesFile = reportService.createNoticesReportFile(new File(detectConfiguration.getProperty(DetectProperty.DETECT_NOTICES_REPORT_PATH)), detectProject.getProjectName(), detectProject.getProjectVersion());
                    if (noticesFile != null) {
                        logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));
                    }
                }
            }

            if ((null != detectProject.getBdioFiles() && !detectProject.getBdioFiles().isEmpty()) || !detectConfiguration.getBooleanProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_DISABLED)) {
                // only log BOM URL if we have updated it in some way
                final ProjectService projectService = hubServiceWrapper.createProjectService();
                final HubService hubService = hubServiceWrapper.createHubService();
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

    public void waitForBomUpdate(final CodeLocationService codeLocationService, final HubService hubService, final ScanStatusService scanStatusService) throws IntegrationException, InterruptedException {
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

    public ProjectVersionView ensureProjectVersionExists(final DetectProject detectProject, final ProjectService projectService) throws IntegrationException {
        final ProjectRequestBuilder projectRequestBuilder = new DetectProjectRequestBuilder(detectConfiguration, detectProject);
        final ProjectRequest projectRequest = projectRequestBuilder.build();

        final ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersionAndCreateIfNeeded(projectRequest);
        if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_VERSION_UPDATE)) {
            logger.debug("Updating Project and Version information to " + projectRequest.toString());
            projectService.updateProjectAndVersion(projectVersionWrapper.getProjectView(), projectRequest);
        }
        return projectVersionWrapper.getProjectVersionView();
    }

    @Override
    public ExitCodeType getExitCodeType() {
        return exitCodeType;
    }
}
