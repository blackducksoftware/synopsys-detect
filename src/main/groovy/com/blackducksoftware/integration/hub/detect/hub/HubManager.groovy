/*
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
package com.blackducksoftware.integration.hub.detect.hub

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.exception.IntegrationException
import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest
import com.blackducksoftware.integration.hub.api.generated.view.CodeLocationView
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView
import com.blackducksoftware.integration.hub.api.view.ScanSummaryView
import com.blackducksoftware.integration.hub.configuration.HubServerConfig
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.exception.DoesNotExistException
import com.blackducksoftware.integration.hub.rest.exception.IntegrationRestException
import com.blackducksoftware.integration.hub.service.CodeLocationService
import com.blackducksoftware.integration.hub.service.HubService
import com.blackducksoftware.integration.hub.service.ProjectService
import com.blackducksoftware.integration.hub.service.ReportService
import com.blackducksoftware.integration.hub.service.ScanStatusService
import com.blackducksoftware.integration.hub.service.SignatureScannerService
import com.blackducksoftware.integration.hub.service.model.PolicyStatusDescription
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder
import com.blackducksoftware.integration.hub.service.model.ProjectVersionWrapper

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HubManager implements ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(HubManager.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    BdioUploader bdioUploader

    @Autowired
    HubSignatureScanner hubSignatureScanner

    @Autowired
    PolicyChecker policyChecker

    @Autowired
    HubServiceWrapper hubServiceWrapper

    private ExitCodeType exitCodeType = ExitCodeType.SUCCESS;

    public ProjectVersionView updateHubProjectVersion(DetectProject detectProject, List<File> createdBdioFiles) {
        ProjectService projectService = hubServiceWrapper.createProjectService()
        ProjectVersionView projectVersionView = ensureProjectVersionExists(detectProject, projectService)
        if (createdBdioFiles) {
            HubServerConfig hubServerConfig = hubServiceWrapper.hubServerConfig
            CodeLocationService codeLocationService = hubServiceWrapper.createCodeLocationService()
            bdioUploader.uploadBdioFiles(hubServerConfig, codeLocationService, detectProject, createdBdioFiles)
        } else {
            logger.debug('Did not create any bdio files.')
        }

        if (!detectConfiguration.getHubSignatureScannerDisabled()) {
            HubServerConfig hubServerConfig = hubServiceWrapper.hubServerConfig
            SignatureScannerService signatureScannerService = hubServiceWrapper.createSignatureScannerService()
            ProjectVersionView scanProject = hubSignatureScanner.scanPaths(hubServerConfig, signatureScannerService, detectProject)
            if (!projectVersionView) {
                projectVersionView = scanProject
            }
        }
        return projectVersionView
    }

    public void performPostHubActions(DetectProject detectProject, ProjectVersionView projectVersionView) throws DetectUserFriendlyException {
        try {
            if (detectConfiguration.getPolicyCheck() || detectConfiguration.getRiskReportPdf() || detectConfiguration.getNoticesReport()) {
                ProjectService projectService = hubServiceWrapper.createProjectService()
                ScanStatusService scanStatusService = hubServiceWrapper.createScanStatusService()

                waitForBomUpdate(hubServiceWrapper.createHubService(), scanStatusService, projectVersionView)

                if (detectConfiguration.getPolicyCheck()) {
                    PolicyStatusDescription policyStatusDescription = policyChecker.getPolicyStatus(projectService, projectVersionView)
                    logger.info(policyStatusDescription.policyStatusMessage)
                    if (policyChecker.policyViolated(policyStatusDescription)) {
                        exitCodeType = ExitCodeType.FAILURE_POLICY_VIOLATION;
                    }
                }

                if (detectConfiguration.getRiskReportPdf()) {
                    ReportService reportService = hubServiceWrapper.createReportService()
                    logger.info("Creating risk report pdf")
                    File pdfFile = reportService.createReportPdfFile(new File(detectConfiguration.riskReportPdfOutputDirectory), detectProject.projectName, detectProject.projectVersionName)
                    logger.info("Created risk report pdf : ${pdfFile.getCanonicalPath()}")
                }

                if (detectConfiguration.getNoticesReport()) {
                    ReportService reportService = hubServiceWrapper.createReportService()
                    logger.info("Creating notices report")
                    File noticesFile = reportService.createNoticesReportFile(new File(detectConfiguration.noticesReportOutputDirectory), detectProject.projectName, detectProject.projectVersionName)
                    if (noticesFile != null) {
                        logger.info("Created notices report : ${noticesFile.getCanonicalPath()}")
                    }
                }
            }

            if (detectProject.getDetectCodeLocations() && !detectConfiguration.getHubSignatureScannerDisabled()) {
                // only log BOM URL if we have updated it in some way
                ProjectService projectService = hubServiceWrapper.createProjectService()
                HubService hubService = hubServiceWrapper.createHubService()
                ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersion(detectProject.getProjectName(), detectProject.getProjectVersionName())
                String componentsLink = hubService.getFirstLinkSafely(projectVersionWrapper.getProjectVersionView(), ProjectVersionView.COMPONENTS_LINK)
                logger.info("To see your results, follow the URL: ${componentsLink}")
            } else {
                logger.debug('Found no code locations and did not run a scan.')
            }
        } catch (IllegalStateException e) {
            throw new DetectUserFriendlyException("Your Hub configuration is not valid: ${e.message}", e, ExitCodeType.FAILURE_HUB_CONNECTIVITY)
        } catch (IntegrationRestException e) {
            throw new DetectUserFriendlyException(e.message, e, ExitCodeType.FAILURE_HUB_CONNECTIVITY)
        } catch (Exception e) {
            throw new DetectUserFriendlyException("There was a problem: ${e.message}", e, ExitCodeType.FAILURE_GENERAL_ERROR)
        }
    }

    public void waitForBomUpdate(HubService hubService, ScanStatusService scanStatusService, ProjectVersionView version) {
        List<CodeLocationView> allCodeLocations = hubService.getAllResponses(version, ProjectVersionView.CODELOCATIONS_LINK_RESPONSE)
        List<ScanSummaryView> scanSummaryViews = []
        allCodeLocations.each {
            String scansLink = hubService.getFirstLinkSafely(it, CodeLocationView.SCANS_LINK)
            if (StringUtils.isNotBlank(scansLink)) {
                List<ScanSummaryView> codeLocationScanSummaryViews = hubService.getResponses(scansLink, ScanSummaryView.class, true)
                scanSummaryViews.addAll(codeLocationScanSummaryViews)
            }
        }
        logger.info("Waiting for the BOM to be updated")
        scanStatusService.assertScansFinished(scanSummaryViews)
        logger.info("The BOM has been updated")
    }

    public ProjectVersionView ensureProjectVersionExists(DetectProject detectProject, ProjectService projectService) {
        ProjectRequestBuilder builder = new ProjectRequestBuilder()
        builder.setProjectName(detectProject.getProjectName())
        builder.setVersionName(detectProject.getProjectVersionName())
        builder.setProjectLevelAdjustments(detectConfiguration.getProjectLevelMatchAdjustments())
        builder.setPhase(detectConfiguration.getProjectVersionPhase())
        builder.setDistribution(detectConfiguration.getProjectVersionDistribution())
        int projectTier = detectConfiguration.projectTier
        if (projectTier > 0) {
            builder.setProjectTier(projectTier)
        }
        ProjectRequest projectRequest = builder.build()

        ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersionAndCreateIfNeeded(projectRequest)
        projectVersionWrapper.projectVersionView
    }

    public void manageExistingCodeLocations(List<String> codeLocationNames) {
        if (!detectConfiguration.hubOfflineMode) {
            CodeLocationService codeLocationService = hubServiceWrapper.createCodeLocationService()
            for (String codeLocationName : codeLocationNames) {
                try {
                    CodeLocationView codeLocationView = codeLocationService.getCodeLocationByName(codeLocationName)
                    if (detectConfiguration.projectCodeLocationDeleteOldNames) {
                        try {
                            codeLocationService.deleteCodeLocation(codeLocationView);
                            logger.info("Deleted code location '${codeLocationName}'")
                        } catch (IntegrationException e) {
                            logger.error("Not able to delete the code location '${codeLocationName}': ${e.message}")
                        }
                    } else {
                        logger.warn("Found a code location with a naming pattern that is no longer supported: ${codeLocationName}. This code location may need to be removed to avoid duplicate entries in the Bill of Materials. You can run with --detect.project.codelocation.delete.old.names=true which will automatically delete these code locations, but please USE CAUTION.")
                    }
                } catch (DoesNotExistException e) {
                    logger.debug("Didn't find the code location ${codeLocationName} - this is a good thing!")
                } catch (IntegrationException e) {
                    logger.error("Error finding the code location name ${codeLocationName}: ${e.message}")
                }
            }
        }
    }

    @Override
    public ExitCodeType getExitCodeType() {
        return exitCodeType;
    }
}