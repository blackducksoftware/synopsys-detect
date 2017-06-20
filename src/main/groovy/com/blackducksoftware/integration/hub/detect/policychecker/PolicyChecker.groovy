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

import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.model.enumeration.VersionBomPolicyStatusOverallStatusEnum
import com.blackducksoftware.integration.hub.model.view.VersionBomPolicyStatusView
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger
import com.google.gson.Gson
import com.google.gson.stream.JsonReader

class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class)

    private HubServicesFactory hubServicesFactory

    static final int TIMEOUT = 300000

    @Autowired
    Gson gson

    public PolicyChecker(HubServicesFactory hubServicesFactory) {
        this.hubServicesFactory = hubServicesFactory
    }

    //TODO Add functionality for policy violations
    public VersionBomPolicyStatusOverallStatusEnum checkForPolicyViolations(Slf4jIntLogger slf4jIntLogger, SimpleBdioDocument bdioDocument) {
        PolicyStatusDataService policyCheck = hubServicesFactory.createPolicyStatusDataService(slf4jIntLogger)

        ScanStatusDataService scanStatusDataService = hubServicesFactory.createScanStatusDataService(slf4jIntLogger, TIMEOUT)
        scanStatusDataService.assertBomImportScanStartedThenFinished(bdioDocument.project.name, bdioDocument.project.version)
        VersionBomPolicyStatusView policyStatus = policyCheck.getPolicyStatusForProjectAndVersion(bdioDocument.project.name, bdioDocument.project.version)

        policyStatus.overallStatus
    }

    public VersionBomPolicyStatusOverallStatusEnum checkForPolicyViolations(Slf4jIntLogger slf4jIntLogger, File bdioDocument) {
        checkForPolicyViolations(slf4jIntLogger, convertFromJsonToSimpleBdioDocument(bdioDocument))
    }

    private SimpleBdioDocument convertFromJsonToSimpleBdioDocument(File bdioDocument) {
        gson.fromJson(new JsonReader(new FileReader(bdioDocument)), SimpleBdioDocument.class)
    }
}
