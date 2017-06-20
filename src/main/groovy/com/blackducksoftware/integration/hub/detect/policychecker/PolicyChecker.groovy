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

import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.model.enumeration.VersionBomPolicyStatusOverallStatusEnum
import com.blackducksoftware.integration.hub.model.view.VersionBomPolicyStatusView
import com.google.gson.Gson
import com.google.gson.stream.JsonReader

class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class)

    private ScanStatusDataService scanStatusDataService
    private PolicyStatusDataService policyStatusDataService
    private Gson gson

    public PolicyChecker(ScanStatusDataService scanStatusDataService, PolicyStatusDataService policyStatusDataService, Gson gson) {
        this.scanStatusDataService = scanStatusDataService
    }

    public VersionBomPolicyStatusOverallStatusEnum checkForPolicyViolations(SimpleBdioDocument bdioDocument) {
        scanStatusDataService.assertBomImportScanStartedThenFinished(bdioDocument.project.name, bdioDocument.project.version)
        VersionBomPolicyStatusView policyStatus = policyStatusDataService.getPolicyStatusForProjectAndVersion(bdioDocument.project.name, bdioDocument.project.version)

        policyStatus.overallStatus
    }

    public VersionBomPolicyStatusOverallStatusEnum checkForPolicyViolations(File bdioDocument) {
        checkForPolicyViolations(convertFromJsonToSimpleBdioDocument(bdioDocument))
    }

    private SimpleBdioDocument convertFromJsonToSimpleBdioDocument(File bdioDocument) {
        gson.fromJson(new JsonReader(new FileReader(bdioDocument)), SimpleBdioDocument.class)
    }
}
