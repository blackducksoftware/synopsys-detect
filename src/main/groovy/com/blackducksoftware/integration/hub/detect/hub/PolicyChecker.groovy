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
import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationRequestService
import com.blackducksoftware.integration.hub.api.item.MetaService
import com.blackducksoftware.integration.hub.api.scan.ScanSummaryRequestService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDescription
import com.blackducksoftware.integration.hub.dataservice.project.ProjectDataService
import com.blackducksoftware.integration.hub.dataservice.project.ProjectVersionWrapper
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectProject
import com.blackducksoftware.integration.hub.detect.exception.DetectException
import com.blackducksoftware.integration.hub.model.enumeration.VersionBomPolicyStatusOverallStatusEnum
import com.blackducksoftware.integration.hub.model.view.CodeLocationView
import com.blackducksoftware.integration.hub.model.view.ScanSummaryView
import com.blackducksoftware.integration.hub.model.view.VersionBomPolicyStatusView
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger

@Component
class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    HubManager hubManager

    /**
     * For the given DetectProject, find the matching Hub project/version, then
     * all of its code locations, then all of their scan summaries, wait until
     * they are all complete, then get the policy status.
     */
    public PolicyStatusDescription getPolicyStatus(HubServicesFactory hubServicesFactory, DetectProject detectProject) throws DetectException {
        Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger)

        ProjectDataService projectDataService = hubServicesFactory.createProjectDataService(slf4jIntLogger)
        CodeLocationRequestService codeLocationRequestService = hubServicesFactory.createCodeLocationRequestService(slf4jIntLogger)
        ScanSummaryRequestService scanSummaryRequestService = hubServicesFactory.createScanSummaryRequestService()
        MetaService metaService = hubServicesFactory.createMetaService(slf4jIntLogger)
        ScanStatusDataService scanStatusDataService = hubServicesFactory.createScanStatusDataService(slf4jIntLogger, detectConfiguration.policyCheckTimeout)
        PolicyStatusDataService policyStatusDataService = hubServicesFactory.createPolicyStatusDataService(slf4jIntLogger)

        String projectName = detectProject.projectName
        String projectVersionName = detectProject.projectVersionName
        ProjectVersionWrapper projectVersion = null
        try {
            projectVersion = projectDataService.getProjectVersion(projectName, projectVersionName)
        } catch (IntegrationException e) {
            throw new DetectException("Not able to find ${projectName}/${projectVersionName}: ${e.message}")
        }

        List<CodeLocationView> allCodeLocations = codeLocationRequestService.getAllCodeLocationsForProjectVersion(projectVersion.projectVersionView)
        List<ScanSummaryView> scanSummaryViews = []
        allCodeLocations.each {
            String scansLink = metaService.getFirstLinkSafely(it, MetaService.SCANS_LINK)
            List<ScanSummaryView> codeLocationScanSummaryViews = scanSummaryRequestService.getAllScanSummaryItems(scansLink)
            scanSummaryViews.addAll(codeLocationScanSummaryViews)
        }

        scanStatusDataService.assertScansFinished(scanSummaryViews)

        VersionBomPolicyStatusView versionBomPolicyStatusView = policyStatusDataService.getPolicyStatusForProjectAndVersion(projectName, projectVersionName)
        PolicyStatusDescription policyStatusDescription = new PolicyStatusDescription(versionBomPolicyStatusView)

        VersionBomPolicyStatusOverallStatusEnum statusEnum = VersionBomPolicyStatusOverallStatusEnum.NOT_IN_VIOLATION
        if (policyStatusDescription.getCountInViolation()?.value > 0) {
            statusEnum = VersionBomPolicyStatusOverallStatusEnum.IN_VIOLATION
        } else if (policyStatusDescription.getCountInViolationOverridden()?.value > 0) {
            statusEnum = VersionBomPolicyStatusOverallStatusEnum.IN_VIOLATION_OVERRIDDEN
        }
        logger.info("Policy Status: ${statusEnum.name()}")
        policyStatusDescription
    }
}
