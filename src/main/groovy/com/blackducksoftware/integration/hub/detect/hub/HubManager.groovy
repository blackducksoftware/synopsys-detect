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

import com.blackducksoftware.integration.hub.api.bom.BomImportRequestService
import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationRequestService
import com.blackducksoftware.integration.hub.api.item.MetaService
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService
import com.blackducksoftware.integration.hub.api.scan.ScanSummaryRequestService
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDescription
import com.blackducksoftware.integration.hub.dataservice.project.ProjectDataService
import com.blackducksoftware.integration.hub.dataservice.project.ProjectVersionWrapper
import com.blackducksoftware.integration.hub.dataservice.report.RiskReportDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.exception.DoesNotExistException
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.model.request.ProjectRequest
import com.blackducksoftware.integration.hub.model.view.CodeLocationView
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView
import com.blackducksoftware.integration.hub.model.view.ProjectView
import com.blackducksoftware.integration.hub.model.view.ScanSummaryView
import com.blackducksoftware.integration.hub.request.builder.ProjectRequestBuilder

@Component
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

    public int performPostActions(DetectProject detectProject, List<File> createdBdioFiles) {
        def postActionResult = 0
        try {
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
            //            if (!detectConfiguration.getHubSignatureScannerDisabled()) {
            //                ProjectVersionView scanProject = hubSignatureScanner.scanPaths(hubServerConfig, hubServicesFactory.createCLIDataService(slf4jIntLogger, 120000L), detectProject)
            //                if (!projectVersionView) {
            //                    projectVersionView = scanProject
            //                }
            //            }

            if (detectConfiguration.getPolicyCheck() || detectConfiguration.getRiskreportPdf() || detectConfiguration.getNoticesReport()) {
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

            if (detectConfiguration.getRiskreportPdf()) {
                RiskReportDataService riskReportDataService = hubServiceWrapper.createRiskReportDataService()
                logger.info("Creating risk report pdf")
                File pdfFile = riskReportDataService.createReportPdfFile(new File("."), detectProject.projectName, detectProject.projectVersionName)
                logger.info("Created risk report pdf : ${pdfFile.getCanonicalPath()}")
            }

            if (detectConfiguration.getNoticeReport()) {
                RiskReportDataService riskReportDataService = hubServicesFactory.createRiskReportDataService(slf4jIntLogger, 30000)
                logger.info("Creating notice report")
                File noticeFile = riskReportDataService.createNoticesReportFile(new File("."), detectProject.projectName, detectProject.projectVersionName);
                if (noticeFile != null){
                    logger.info("Created notice report : ${noticeFile.getCanonicalPath()}")
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

    private void performScan(DetectProject detectProject) {
        if (!detectConfiguration.getHubSignatureScannerDisabled()) {
            ProjectVersionView scanProject = hubSignatureScanner.scanPaths(hubServerConfig, hubServicesFactory.createCLIDataService(slf4jIntLogger, 120000L), detectProject)
            if (!projectVersionView) {
                projectVersionView = scanProject
            }
        }
    }

    public void waitForBomUpdate(ProjectDataService projectDataService, CodeLocationRequestService codeLocationRequestService, MetaService metaService, ScanSummaryRequestService scanSummaryRequestService, ScanStatusDataService scanStatusDataService, ProjectVersionView version){
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
}
