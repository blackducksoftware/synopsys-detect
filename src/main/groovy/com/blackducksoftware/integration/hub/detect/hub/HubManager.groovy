/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
import com.blackducksoftware.integration.hub.api.bom.BomImportRequestService
import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationRequestService
import com.blackducksoftware.integration.hub.api.item.MetaService
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService
import com.blackducksoftware.integration.hub.api.scan.ScanSummaryRequestService
import com.blackducksoftware.integration.hub.dataservice.cli.CLIDataService
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDescription
import com.blackducksoftware.integration.hub.dataservice.project.ProjectDataService
import com.blackducksoftware.integration.hub.dataservice.project.ProjectVersionWrapper
import com.blackducksoftware.integration.hub.dataservice.report.RiskReportDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.CodeLocationType
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.exception.DoesNotExistException
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.model.request.ProjectRequest
import com.blackducksoftware.integration.hub.model.view.CodeLocationView
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView
import com.blackducksoftware.integration.hub.model.view.ProjectView
import com.blackducksoftware.integration.hub.model.view.ScanSummaryView
import com.blackducksoftware.integration.hub.request.builder.ProjectRequestBuilder

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HubManager {
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

    public ProjectVersionView updateHubProjectVersion(DetectProject detectProject, List<File> createdBdioFiles) {
        ProjectRequestService projectRequestService = hubServiceWrapper.createProjectRequestService()
        ProjectVersionRequestService projectVersionRequestService = hubServiceWrapper.createProjectVersionRequestService()
        ProjectVersionView projectVersionView = ensureProjectVersionExists(detectProject, projectRequestService, projectVersionRequestService)
        if (createdBdioFiles) {
            HubServerConfig hubServerConfig = hubServiceWrapper.hubServerConfig
            BomImportRequestService bomImportRequestService = hubServiceWrapper.createBomImportRequestService()
            PhoneHomeDataService phoneHomeDataService = hubServiceWrapper.createPhoneHomeDataService()
            bdioUploader.uploadBdioFiles(hubServerConfig, bomImportRequestService, phoneHomeDataService, createdBdioFiles)
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

    public int performPostHubActions(DetectProject detectProject, ProjectVersionView projectVersionView) {
        def postActionResult = 0
        try {

            if (detectConfiguration.getPolicyCheck() || detectConfiguration.getRiskReportPdf() || detectConfiguration.getNoticesReport()) {
                ProjectDataService projectDataService = hubServiceWrapper.createProjectDataService()
                CodeLocationRequestService codeLocationRequestService = hubServiceWrapper.createCodeLocationRequestService()
                MetaService metaService = hubServiceWrapper.createMetaService()
                ScanSummaryRequestService scanSummaryRequestService = hubServiceWrapper.createScanSummaryRequestService()
                ScanStatusDataService scanStatusDataService = hubServiceWrapper.createScanStatusDataService()

                waitForBomUpdate(projectDataService, codeLocationRequestService, metaService, scanSummaryRequestService, scanStatusDataService, projectVersionView)
            }

            if (detectConfiguration.getPolicyCheck()) {
                PolicyStatusDataService policyStatusDataService = hubServiceWrapper.createPolicyStatusDataService()
                PolicyStatusDescription policyStatus = policyChecker.getPolicyStatus(policyStatusDataService, projectVersionView)
                logger.info(policyStatus.policyStatusMessage)
                if (policyStatus.getCountInViolation()?.value > 0) {
                    postActionResult = 1
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

            if (detectProject.getDetectCodeLocations() && !detectConfiguration.getHubSignatureScannerDisabled()) {
                // only log BOM URL if we have updated it in some way
                ProjectDataService projectDataService = hubServiceWrapper.createProjectDataService()
                ProjectVersionWrapper projectVersionWrapper = projectDataService.getProjectVersion(detectProject.getProjectName(), detectProject.getProjectVersionName())
                MetaService metaService = hubServiceWrapper.createMetaService()
                String componentsLink = metaService.getFirstLinkSafely(projectVersionWrapper.getProjectVersionView(), MetaService.COMPONENTS_LINK)
                logger.info("To see your results, follow the URL: ${componentsLink}")
            } else {
                logger.debug('Found no code locations and did not run a scan.')
            }
        } catch (IllegalStateException e) {
            logger.error("Your Hub configuration is not valid: ${e.message}")
            logger.debug(e.getMessage(), e)
        } catch (Exception e) {
            logger.error("There was a problem communicating with the Hub : ${e.message}")
            logger.debug(e.getMessage(), e)
        }
        postActionResult
    }

    public void waitForBomUpdate(ProjectDataService projectDataService, CodeLocationRequestService codeLocationRequestService, MetaService metaService, ScanSummaryRequestService scanSummaryRequestService, ScanStatusDataService scanStatusDataService, ProjectVersionView version) {
        List<CodeLocationView> allCodeLocations = codeLocationRequestService.getAllCodeLocationsForProjectVersion(version)
        List<ScanSummaryView> scanSummaryViews = []
        allCodeLocations.each {
            String scansLink = metaService.getFirstLinkSafely(it, MetaService.SCANS_LINK)
            List<ScanSummaryView> codeLocationScanSummaryViews = scanSummaryRequestService.getAllScanSummaryItems(scansLink)
            scanSummaryViews.addAll(codeLocationScanSummaryViews)
        }
        logger.info("Waiting for the BOM to be updated")
        scanStatusDataService.assertScansFinished(scanSummaryViews)
        logger.info("The BOM has been updated")
    }

    public ProjectVersionView ensureProjectVersionExists(DetectProject detectProject, ProjectRequestService projectRequestService, ProjectVersionRequestService projectVersionRequestService) {
        ProjectRequestBuilder builder = new ProjectRequestBuilder()
        builder.setProjectName(detectProject.getProjectName())
        builder.setVersionName(detectProject.getProjectVersionName())
        builder.setProjectLevelAdjustments(detectConfiguration.getProjectLevelMatchAdjustments())
        builder.setPhase(detectConfiguration.getProjectVersionPhase())
        builder.setDistribution(detectConfiguration.getProjectVersionDistribution())
        ProjectRequest projectRequest = builder.build()
        ProjectView project = null
        try {
            project = projectRequestService.getProjectByName(projectRequest.getName())
        } catch (final DoesNotExistException e) {
            final String projectURL = projectRequestService.createHubProject(projectRequest)
            project = projectRequestService.getItem(projectURL, ProjectView.class)
        }
        ProjectVersionView projectVersionView = null
        try {
            projectVersionView = projectVersionRequestService.getProjectVersion(project, projectRequest.getVersionRequest().getVersionName())
        } catch (final DoesNotExistException e) {
            final String versionURL = projectVersionRequestService.createHubVersion(project, projectRequest.getVersionRequest())
            projectVersionView = projectVersionRequestService.getItem(versionURL, ProjectVersionView.class)
        }
    }

    public CodeLocationView logOldCodeLocationNameExists(DetectProject detectProject, BomToolType bomToolType, CodeLocationType codeLocationType, String sourcePath, String prefix) {
        if (!detectConfiguration.hubOfflineMode) {
            String oldCodeLocationName = generateOldCodeLocationName(detectProject, bomToolType, codeLocationType, sourcePath, prefix)
            try {
                CodeLocationView codeLocationView = hubServiceWrapper.createCodeLocationRequestService().getCodeLocationByName(oldCodeLocationName)
                logger.warn("Found same code location with old naming pattern: ${oldCodeLocationName}. You may remove old code location if desired")
                return codeLocationView
            } catch (IntegrationException e) {
                return null
            }
        }
    }

    public boolean deleteExistingCodeLocation(CodeLocationView codeLocationView) {
        if (!detectConfiguration.hubOfflineMode) {
            try {
                hubServiceWrapper.createCodeLocationRequestService().deleteCodeLocation(codeLocationView);
                logger.info("Deleted code location '${codeLocationView.name}'")
                return true
            } catch (IntegrationException e) {
                return false
            }
        }
    }

    private String generateOldCodeLocationName(DetectProject detectProject, BomToolType bomToolType, CodeLocationType codeLocationType, String sourcePath, String prefix) {
        if (CodeLocationType.SCAN.toString().equals(codeLocationType.toString())) {
            return detectProject.getScanCodeLocationName('', '', sourcePath, prefix)
        } else if (CodeLocationType.BOM.toString().equals(codeLocationType.toString())) {
            return detectProject.getBomToolCodeLocationName(bomToolType, sourcePath, prefix)
        }

        return ''
    }
}
