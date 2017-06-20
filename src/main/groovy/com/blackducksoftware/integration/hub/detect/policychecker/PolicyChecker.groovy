/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.policychecker

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.model.enumeration.VersionBomPolicyStatusOverallStatusEnum
import com.blackducksoftware.integration.hub.model.view.VersionBomPolicyStatusView
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger

@Component
class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class)

    @Autowired
    DetectConfiguration detectConfiguration

    //TODO Add functionality for policy violations
    public VersionBomPolicyStatusOverallStatusEnum checkForPolicyViolations(String projectName, String projectVersion) {
        Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger)
        HubServerConfig hubServerConfig = createBuilder(slf4jIntLogger).build()
        RestConnection restConnection = hubServerConfig.createCredentialsRestConnection(slf4jIntLogger)

        HubServicesFactory hubServicesFactory = new HubServicesFactory(restConnection);

        PolicyStatusDataService policyCheck = hubServicesFactory.createPolicyStatusDataService(logger)

        ScanStatusDataService scanStatusDataService = hubServicesFactory.createScanStatusDataService(logger, 300000)
        scanStatusDataService.assertBomImportScanStartedThenFinished(projectName, projectVersion)
        VersionBomPolicyStatusView policyStatus = policyCheck.getPolicyStatusForProjectAndVersion(projectName, projectVersion)

        policyStatus.overallStatus
    }

    private HubServerConfigBuilder createBuilder(Slf4jIntLogger slf4jIntLogger) {
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

        hubServerConfigBuilder
    }
}
