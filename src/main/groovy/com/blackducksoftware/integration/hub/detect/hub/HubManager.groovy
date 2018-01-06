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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.exception.IntegrationException
import com.blackducksoftware.integration.hub.api.bom.BomImportService
import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationService
import com.blackducksoftware.integration.hub.api.project.ProjectService
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionService
import com.blackducksoftware.integration.hub.api.scan.ScanSummaryService
import com.blackducksoftware.integration.hub.api.view.MetaHandler
import com.blackducksoftware.integration.hub.dataservice.cli.CLIDataService
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDescription
import com.blackducksoftware.integration.hub.dataservice.project.ProjectDataService
import com.blackducksoftware.integration.hub.dataservice.project.ProjectVersionWrapper
import com.blackducksoftware.integration.hub.dataservice.report.RiskReportDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.exception.DoesNotExistException
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.model.request.ProjectRequest
import com.blackducksoftware.integration.hub.model.view.CodeLocationView
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView
import com.blackducksoftware.integration.hub.model.view.ProjectView
import com.blackducksoftware.integration.hub.model.view.ScanSummaryView
import com.blackducksoftware.integration.hub.request.builder.ProjectRequestBuilder
import com.blackducksoftware.integration.hub.service.HubService

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
        ProjectVersionService projectVersionService = hubServiceWrapper.createProjectVersionService()
        ProjectVersionView projectVersionView = ensureProjectVersionExists(detectProject, projectService, projectVersionService)
        if (createdBdioFiles) {
            HubServerConfig hubServerConfig = hubServiceWrapper.hubServerConfig
            BomImportService bomImportService = hubServiceWrapper.createBomImportService()
            PhoneHomeDataService phoneHomeDataService = hubServiceWrapper.createPhoneHomeDataService()
            bdioUploader.uploadBdioFiles(hubServerConfig, bomImportService, phoneHomeDataService, detectProject, createdBdioFiles)
        } else {
            logger.debug('Did not create any bdio files.')
        }

        if (!detectConfiguration.getHubSignatureScannerDisabled()) {
            HubServerConfig hubServerConfig = hubServiceWrapper.hubServerConfig
            CLIDataService cliDataService = hubServiceWrapper.createCliDataService()
            ProjectVersionView scanProject = hubSignatureScanner.scanPaths(hubServerConfig, cliDataService, detectProject)
            if (!projectVersionView) {
                projectVersionView = scanProject
            }
        }
        return projectVersionView
    }

    public void performPostHubActions(DetectProject detectProject, ProjectVersionView projectVersionView) throws DetectUserFriendlyException {
        try {
            if (detectConfiguration.getPolicyCheck() || detectConfiguration.getRiskReportPdf() || detectConfiguration.getNoticesReport()) {
                ProjectDataService projectDataService = hubServiceWrapper.createProjectDataService()
                CodeLocationService codeLocationService = hubServiceWrapper.createCodeLocationService()
                ScanSummaryService scanSummaryService = hubServiceWrapper.createScanSummaryService()
                ScanStatusDataService scanStatusDataService = hubServiceWrapper.createScanStatusDataService()

                waitForBomUpdate(projectDataService, codeLocationService, scanSummaryService, scanStatusDataService, projectVersionView)

                if (detectConfiguration.getPolicyCheck()) {
                    PolicyStatusDataService policyStatusDataService = hubServiceWrapper.createPolicyStatusDataService()
                    PolicyStatusDescription policyStatusDescription = policyChecker.getPolicyStatus(policyStatusDataService, projectVersionView)
                    logger.info(policyStatusDescription.policyStatusMessage)
                    if (policyChecker.policyViolated(policyStatusDescription)) {
                        exitCodeType = ExitCodeType.FAILURE_POLICY_VIOLATION;
                    }
                }

                if (detectConfiguration.getRiskReportPdf()) {
                    RiskReportDataService riskReportDataService = hubServiceWrapper.createRiskReportDataService()
                    logger.info("Creating risk report pdf")
                    File pdfFile = riskReportDataService.createReportPdfFile(new File(detectConfiguration.riskReportPdfOutputDirectory), detectProject.projectName, detectProject.projectVersionName)
                    logger.info("Created risk report pdf : ${pdfFile.getCanonicalPath()}")
                }

                if (detectConfiguration.getNoticesReport()) {
                    RiskReportDataService riskReportDataService = hubServiceWrapper.createRiskReportDataService()
                    logger.info("Creating notices report")
                    File noticesFile = riskReportDataService.createNoticesReportFile(new File(detectConfiguration.noticesReportOutputDirectory), detectProject.projectName, detectProject.projectVersionName)
                    if (noticesFile != null) {
                        logger.info("Created notices report : ${noticesFile.getCanonicalPath()}")
                    }
                }
            }

            if (detectProject.getDetectCodeLocations() && !detectConfiguration.getHubSignatureScannerDisabled()) {
                // only log BOM URL if we have updated it in some way
                ProjectDataService projectDataService = hubServiceWrapper.createProjectDataService()
                HubService hubService = hubServiceWrapper.createHubService()
                ProjectVersionWrapper projectVersionWrapper = projectDataService.getProjectVersion(detectProject.getProjectName(), detectProject.getProjectVersionName())
                String componentsLink = hubService.getFirstLinkSafely(projectVersionWrapper.getProjectVersionView(), MetaHandler.COMPONENTS_LINK)
                logger.info("To see your results, follow the URL: ${componentsLink}")
            } else {
                logger.debug('Found no code locations and did not run a scan.')
            }
        } catch (IllegalStateException e) {
            throw new DetectUserFriendlyException("Your Hub configuration is not valid: ${e.message}", e, ExitCodeType.FAILURE_HUB_CONNECTIVITY)
        } catch (Exception e) {
            throw new DetectUserFriendlyException("There was a problem communicating with the Hub: ${e.message}", e, ExitCodeType.FAILURE_HUB_CONNECTIVITY)
        }
    }

    public void waitForBomUpdate(ProjectDataService projectDataService, CodeLocationService codeLocationService, ScanSummaryService scanSummaryService, ScanStatusDataService scanStatusDataService, ProjectVersionView version) {
        List<CodeLocationView> allCodeLocations = codeLocationService.getAllCodeLocationsForProjectVersion(version)
        List<ScanSummaryView> scanSummaryViews = []
        allCodeLocations.each {
            String scansLink = codeLocationService.getFirstLinkSafely(it, MetaHandler.SCANS_LINK)
            List<ScanSummaryView> codeLocationScanSummaryViews = scanSummaryService.getAllScanSummaryItems(scansLink)
            scanSummaryViews.addAll(codeLocationScanSummaryViews)
        }
        logger.info("Waiting for the BOM to be updated")
        scanStatusDataService.assertScansFinished(scanSummaryViews)
        logger.info("The BOM has been updated")
    }

    public ProjectVersionView ensureProjectVersionExists(DetectProject detectProject, ProjectService projectService, ProjectVersionService projectVersionService) {
        ProjectRequestBuilder builder = new ProjectRequestBuilder()
        builder.setProjectName(detectProject.getProjectName())
        builder.setVersionName(detectProject.getProjectVersionName())
        builder.setProjectLevelAdjustments(detectConfiguration.getProjectLevelMatchAdjustments())
        builder.setPhase(detectConfiguration.getProjectVersionPhase())
        builder.setDistribution(detectConfiguration.getProjectVersionDistribution())
        ProjectRequest projectRequest = builder.build()
        ProjectView project = null
        try {
            project = projectService.getProjectByName(projectRequest.getName())
        } catch (final DoesNotExistException e) {
            final String projectURL = projectService.createHubProject(projectRequest)
            project = projectService.getView(projectURL, ProjectView.class)
        }
        ProjectVersionView projectVersionView = null
        try {
            projectVersionView = projectVersionService.getProjectVersion(project, projectRequest.getVersionRequest().getVersionName())
        } catch (final DoesNotExistException e) {
            final String versionURL = projectVersionService.createHubVersion(project, projectRequest.getVersionRequest())
            projectVersionView = projectVersionService.getView(versionURL, ProjectVersionView.class)
        }
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