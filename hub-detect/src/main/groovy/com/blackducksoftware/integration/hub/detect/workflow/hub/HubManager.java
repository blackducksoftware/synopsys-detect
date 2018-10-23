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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.view.ScanSummaryView;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.exception.HubTimeoutExceededException;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.blackduck.service.HubService;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.ReportService;
import com.synopsys.integration.blackduck.service.ScanStatusService;
import com.synopsys.integration.blackduck.service.model.PolicyStatusDescription;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.exception.IntegrationRestException;

public class HubManager {
    private final Logger logger = LoggerFactory.getLogger(HubManager.class);

    private final BlackDuckBinaryScanner blackDuckBinaryScanner;
    private final DetectBdioUploadService detectBdioUploadService;
    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;
    private final HubServiceManager hubServiceManager;
    private final BlackDuckSignatureScanner blackDuckSignatureScanner;
    private final PolicyChecker policyChecker;

    private ExitCodeType exitCodeType = ExitCodeType.SUCCESS;

    public HubManager(final DetectBdioUploadService detectBdioUploadService, final CodeLocationNameManager codeLocationNameManager, final DetectConfiguration detectConfiguration, final HubServiceManager hubServiceManager,
        final BlackDuckSignatureScanner blackDuckSignatureScanner, final PolicyChecker policyChecker, final BlackDuckBinaryScanner blackDuckBinaryScanner) {
        this.detectBdioUploadService = detectBdioUploadService;
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
        this.hubServiceManager = hubServiceManager;
        this.blackDuckSignatureScanner = blackDuckSignatureScanner;
        this.policyChecker = policyChecker;
        this.blackDuckBinaryScanner = blackDuckBinaryScanner;
    }

    public void performScanActions(final DetectProject detectProject) throws IntegrationException, InterruptedException, DetectUserFriendlyException {
        if (!detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, PropertyAuthority.None)) {
            final HubServerConfig hubServerConfig = hubServiceManager.getHubServerConfig();
            final ExecutorService executorService = Executors.newFixedThreadPool(detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS, PropertyAuthority.None));
            try {
                final ScanJobManager scanJobManager = hubServiceManager.createScanJobManager(executorService);
                blackDuckSignatureScanner.scanPaths(hubServerConfig, scanJobManager, detectProject);
            } finally {
                executorService.shutdownNow();
            }
        }
    }

    public void performBinaryScanActions(final DetectProject detectProject) throws DetectUserFriendlyException {
        if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None))) {
            final String prefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX, PropertyAuthority.None);
            final String suffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX, PropertyAuthority.None);

            final File file = new File(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None));
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

            if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, PropertyAuthority.None)) || detectConfiguration
                                                                                                                                                              .getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF, PropertyAuthority.None)
                    || detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT, PropertyAuthority.None)) {
                waitForBomUpdate(codeLocationService, hubService, scanStatusService);
            }

            if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, PropertyAuthority.None))) {
                final PolicyStatusDescription policyStatusDescription = policyChecker.getPolicyStatus(projectService, projectVersionView);
                logger.info(policyStatusDescription.getPolicyStatusMessage());
                if (policyChecker.policyViolated(policyStatusDescription)) {
                    exitCodeType = ExitCodeType.FAILURE_POLICY_VIOLATION;

                }
            }

            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF, PropertyAuthority.None)) {
                logger.info("Creating risk report pdf");
                final File pdfFile = reportService
                                         .createReportPdfFile(new File(detectConfiguration.getProperty(DetectProperty.DETECT_RISK_REPORT_PDF_PATH, PropertyAuthority.None)), detectProject.getProjectName(), detectProject.getProjectVersion());
                logger.info(String.format("Created risk report pdf: %s", pdfFile.getCanonicalPath()));
            }

            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT, PropertyAuthority.None)) {
                logger.info("Creating notices report");
                final File noticesFile = reportService.createNoticesReportFile(new File(detectConfiguration.getProperty(DetectProperty.DETECT_NOTICES_REPORT_PATH, PropertyAuthority.None)), detectProject.getProjectName(),
                    detectProject.getProjectVersion()
                );
                if (noticesFile != null) {
                    logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));
                }
            }

            if (!detectProject.getBdioFiles().isEmpty() || !detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, PropertyAuthority.None)) {
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
}
