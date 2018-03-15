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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest;
import com.blackducksoftware.integration.hub.api.generated.view.CodeLocationView;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.api.view.ScanSummaryView;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.exception.DoesNotExistException;
import com.blackducksoftware.integration.hub.rest.exception.IntegrationRestException;
import com.blackducksoftware.integration.hub.service.CodeLocationService;
import com.blackducksoftware.integration.hub.service.HubService;
import com.blackducksoftware.integration.hub.service.ProjectService;
import com.blackducksoftware.integration.hub.service.ReportService;
import com.blackducksoftware.integration.hub.service.ScanStatusService;
import com.blackducksoftware.integration.hub.service.SignatureScannerService;
import com.blackducksoftware.integration.hub.service.model.PolicyStatusDescription;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.hub.service.model.ProjectVersionWrapper;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class HubManager implements ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(HubManager.class);

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private BdioUploader bdioUploader;

    @Autowired
    private HubSignatureScanner hubSignatureScanner;

    @Autowired
    private PolicyChecker policyChecker;

    @Autowired
    private HubServiceWrapper hubServiceWrapper;

    private ExitCodeType exitCodeType = ExitCodeType.SUCCESS;

    public ProjectVersionView updateHubProjectVersion(final DetectProject detectProject, final List<File> createdBdioFiles) throws IntegrationException {
        final ProjectService projectService = hubServiceWrapper.createProjectService();
        ProjectVersionView projectVersionView = ensureProjectVersionExists(detectProject, projectService);
        if (null != createdBdioFiles && !createdBdioFiles.isEmpty()) {
            final HubServerConfig hubServerConfig = hubServiceWrapper.getHubServerConfig();
            final CodeLocationService codeLocationService = hubServiceWrapper.createCodeLocationService();
            bdioUploader.uploadBdioFiles(hubServerConfig, codeLocationService, detectProject, createdBdioFiles);
        } else {
            logger.debug("Did not create any bdio files.");
        }

        if (!detectConfiguration.getHubSignatureScannerDisabled()) {
            final HubServerConfig hubServerConfig = hubServiceWrapper.getHubServerConfig();
            final SignatureScannerService signatureScannerService = hubServiceWrapper.createSignatureScannerService();
            final ProjectVersionView scanProject = hubSignatureScanner.scanPaths(hubServerConfig, signatureScannerService, detectProject);
            if (null == projectVersionView) {
                projectVersionView = scanProject;
            }
        }
        return projectVersionView;
    }

    public void performPostHubActions(final DetectProject detectProject, final ProjectVersionView projectVersionView) throws DetectUserFriendlyException {
        try {
            if (detectConfiguration.getPolicyCheck() || detectConfiguration.getRiskReportPdf() || detectConfiguration.getNoticesReport()) {
                final ProjectService projectService = hubServiceWrapper.createProjectService();
                final ScanStatusService scanStatusService = hubServiceWrapper.createScanStatusService();

                waitForBomUpdate(hubServiceWrapper.createHubService(), scanStatusService, projectVersionView);

                if (detectConfiguration.getPolicyCheck()) {
                    final PolicyStatusDescription policyStatusDescription = policyChecker.getPolicyStatus(projectService, projectVersionView);
                    logger.info(policyStatusDescription.getPolicyStatusMessage());
                    if (policyChecker.policyViolated(policyStatusDescription)) {
                        exitCodeType = ExitCodeType.FAILURE_POLICY_VIOLATION;
                    }
                }

                if (detectConfiguration.getRiskReportPdf()) {
                    final ReportService reportService = hubServiceWrapper.createReportService();
                    logger.info("Creating risk report pdf");
                    final File pdfFile = reportService.createReportPdfFile(new File(detectConfiguration.getRiskReportPdfOutputDirectory()), detectProject.getProjectName(), detectProject.getProjectVersionName());
                    logger.info(String.format("Created risk report pdf : %s", pdfFile.getCanonicalPath()));
                }

                if (detectConfiguration.getNoticesReport()) {
                    final ReportService reportService = hubServiceWrapper.createReportService();
                    logger.info("Creating notices report");
                    final File noticesFile = reportService.createNoticesReportFile(new File(detectConfiguration.getNoticesReportOutputDirectory()), detectProject.getProjectName(), detectProject.getProjectVersionName());
                    if (noticesFile != null) {
                        logger.info(String.format("Created notices report : %s", noticesFile.getCanonicalPath()));
                    }
                }
            }

            if (null != detectProject.getDetectCodeLocations() && !detectProject.getDetectCodeLocations().isEmpty() && !detectConfiguration.getHubSignatureScannerDisabled()) {
                // only log BOM URL if we have updated it in some way
                final ProjectService projectService = hubServiceWrapper.createProjectService();
                final HubService hubService = hubServiceWrapper.createHubService();
                final ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersion(detectProject.getProjectName(), detectProject.getProjectVersionName());
                final String componentsLink = hubService.getFirstLinkSafely(projectVersionWrapper.getProjectVersionView(), ProjectVersionView.COMPONENTS_LINK);
                logger.info(String.format("To see your results, follow the URL: %s", componentsLink));
            } else {
                logger.debug("Found no code locations and did not run a scan.");
            }
        } catch (final IllegalStateException e) {
            throw new DetectUserFriendlyException(String.format("Your Hub configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        } catch (final IntegrationRestException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("There was a problem: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    public void waitForBomUpdate(final HubService hubService, final ScanStatusService scanStatusService, final ProjectVersionView version) throws IntegrationException {
        final List<CodeLocationView> allCodeLocations = hubService.getAllResponses(version, ProjectVersionView.CODELOCATIONS_LINK_RESPONSE);
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
        final ProjectRequestBuilder builder = new ProjectRequestBuilder();
        builder.setProjectName(detectProject.getProjectName());
        builder.setVersionName(detectProject.getProjectVersionName());
        builder.setProjectLevelAdjustments(detectConfiguration.getProjectLevelMatchAdjustments());
        builder.setPhase(detectConfiguration.getProjectVersionPhase());
        builder.setDistribution(detectConfiguration.getProjectVersionDistribution());
        builder.setProjectTier(detectConfiguration.getProjectTier());
        builder.setReleaseComments(detectConfiguration.getProjectVersionNotes());
        final ProjectRequest projectRequest = builder.build();

        final ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersionAndCreateIfNeeded(projectRequest);
        return projectVersionWrapper.getProjectVersionView();
    }

    public void manageExistingCodeLocations(final List<String> codeLocationNames) {
        if (!detectConfiguration.getHubOfflineMode()) {
            final CodeLocationService codeLocationService = hubServiceWrapper.createCodeLocationService();
            for (final String codeLocationName : codeLocationNames) {
                try {
                    final CodeLocationView codeLocationView = codeLocationService.getCodeLocationByName(codeLocationName);
                    if (detectConfiguration.getProjectCodeLocationDeleteOldNames()) {
                        try {
                            codeLocationService.deleteCodeLocation(codeLocationView);
                            logger.info(String.format("Deleted code location '%s'", codeLocationName));
                        } catch (final IntegrationException e) {
                            logger.error(String.format("Not able to delete the code location '%s': %s", codeLocationName, e.getMessage()));
                        }
                    } else {
                        logger.warn(String.format(
                                "Found a code location with a naming pattern that is no longer supported: %s. This code location may need to be removed to avoid duplicate entries in the Bill of Materials. You can run with --detect.project.codelocation.delete.old.names=true which will automatically delete these code locations, but please USE CAUTION.",
                                codeLocationName));
                    }
                } catch (final DoesNotExistException e) {
                    logger.debug(String.format("Didn't find the code location %s - this is a good thing!", codeLocationName));
                } catch (final IntegrationException e) {
                    logger.error(String.format("Error finding the code location name %s: %s", codeLocationName, e.getMessage()));
                }
            }
        }
    }

    @Override
    public ExitCodeType getExitCodeType() {
        return exitCodeType;
    }
}
