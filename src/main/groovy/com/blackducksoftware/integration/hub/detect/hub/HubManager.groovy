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

import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationRequestService
import com.blackducksoftware.integration.hub.api.item.MetaService
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService
import com.blackducksoftware.integration.hub.api.scan.ScanSummaryRequestService
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder
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
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger

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

    public HubServicesFactory createHubServicesFactory(Slf4jIntLogger slf4jIntLogger, HubServerConfig hubServerConfig) {
        RestConnection restConnection = hubServerConfig.createCredentialsRestConnection(slf4jIntLogger)

        new HubServicesFactory(restConnection)
    }

    public HubServerConfig createHubServerConfig(Slf4jIntLogger slf4jIntLogger) {
        HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder()
        hubServerConfigBuilder.setHubUrl(detectConfiguration.getHubUrl())
        hubServerConfigBuilder.setTimeout(detectConfiguration.getHubTimeout())
        hubServerConfigBuilder.setUsername(detectConfiguration.getHubUsername())
        hubServerConfigBuilder.setPassword(detectConfiguration.getHubPassword())

        hubServerConfigBuilder.setProxyHost(detectConfiguration.getHubProxyHost())
        hubServerConfigBuilder.setProxyPort(detectConfiguration.getHubProxyPort())
        hubServerConfigBuilder.setProxyUsername(detectConfiguration.getHubProxyUsername())
        hubServerConfigBuilder.setProxyPassword(detectConfiguration.getHubProxyPassword())

        hubServerConfigBuilder.setAutoImportHttpsCertificates(detectConfiguration.getHubAutoImportCertificate())
        hubServerConfigBuilder.setLogger(slf4jIntLogger)

        hubServerConfigBuilder.build()
    }

    public int performPostActions(DetectProject detectProject, List<File> createdBdioFiles) {
        def postActionResult = 0
        try {
            Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger)
            HubServerConfig hubServerConfig = createHubServerConfig(slf4jIntLogger)
            HubServicesFactory hubServicesFactory = createHubServicesFactory(slf4jIntLogger, hubServerConfig)
            ProjectVersionView projectVersionView = ensureProjectVersionExists(detectProject, hubServicesFactory.createProjectRequestService(slf4jIntLogger), hubServicesFactory.createProjectVersionRequestService(slf4jIntLogger))
            if (createdBdioFiles) {
                bdioUploader.uploadBdioFiles(hubServerConfig, hubServicesFactory, createdBdioFiles)
            } else {
                logger.debug('Did not create any bdio files.')
            }
            if (!detectConfiguration.getHubSignatureScannerDisabled()) {
                ProjectVersionView scanProject = hubSignatureScanner.scanPaths(hubServerConfig, hubServicesFactory.createCLIDataService(slf4jIntLogger, 120000L), detectProject)
                if (!projectVersionView) {
                    projectVersionView = scanProject
                }
            }
            if (detectConfiguration.getPolicyCheck() || detectConfiguration.getRiskreportPDF()) {
                waitForBomUpdate(hubServicesFactory.createProjectDataService(slf4jIntLogger), hubServicesFactory.createCodeLocationRequestService(slf4jIntLogger), hubServicesFactory.createMetaService(slf4jIntLogger),
                        hubServicesFactory.createScanSummaryRequestService(), hubServicesFactory.createScanStatusDataService(slf4jIntLogger, detectConfiguration.getPolicyCheckTimeout()), projectVersionView)
            }
            if (detectConfiguration.getPolicyCheck()) {
                PolicyStatusDescription policyStatus = policyChecker.getPolicyStatus(hubServicesFactory.createPolicyStatusDataService(slf4jIntLogger), projectVersionView)
                logger.info(policyStatus.policyStatusMessage)
                if (policyStatus.getCountInViolation()?.value > 0) {
                    postActionResult = 1
                }
            }
            if (detectConfiguration.getRiskreportPDF()) {
                RiskReportDataService riskReportDataService = hubServicesFactory.createRiskReportDataService(slf4jIntLogger, 30000)
                logger.info("Creating risk report pdf")
                File pdfFile = riskReportDataService.createReportPdfFile(new File("."), detectProject.projectName, detectProject.projectVersionName)
                logger.info("Created risk report pdf : ${pdfFile.getCanonicalPath()}")
            }
            if (detectProject.getDetectCodeLocations() && !detectConfiguration.getHubSignatureScannerDisabled()) {
                // only log BOM URL if we have updated it in some way
                ProjectDataService projectDataService = hubServicesFactory.createProjectDataService(slf4jIntLogger)
                ProjectVersionWrapper projectVersionWrapper = projectDataService.getProjectVersion(detectProject.getProjectName(), detectProject.getProjectVersionName())
                MetaService metaService = hubServicesFactory.createMetaService(slf4jIntLogger)
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
