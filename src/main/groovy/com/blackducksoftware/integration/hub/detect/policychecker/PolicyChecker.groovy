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

import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.model.enumeration.VersionBomPolicyStatusOverallStatusEnum
import com.blackducksoftware.integration.hub.model.view.VersionBomPolicyStatusView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader

class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class)

    private ScanStatusDataService scanStatusDataService
    private PolicyStatusDataService policyStatusDataService
    private Gson gson

    public PolicyChecker(ScanStatusDataService scanStatusDataService, PolicyStatusDataService policyStatusDataService, Gson gson) {
        this.scanStatusDataService = scanStatusDataService
        this.policyStatusDataService = policyStatusDataService
        this.gson = gson
    }

    public VersionBomPolicyStatusOverallStatusEnum checkForPolicyViolations(BdioPolicyModel bdioPolicyModel) {
        scanStatusDataService.assertBomImportScanStartedThenFinished(bdioPolicyModel.name, bdioPolicyModel.version)
        VersionBomPolicyStatusView policyStatus = policyStatusDataService.getPolicyStatusForProjectAndVersion(bdioPolicyModel.name, bdioPolicyModel.version)

        policyStatus.overallStatus
    }

    public VersionBomPolicyStatusOverallStatusEnum checkForPolicyViolations(File bdioFile) {
        checkForPolicyViolations(convertFromJsonToSimpleBdioDocument(bdioFile))
    }

    private BdioPolicyModel convertFromJsonToSimpleBdioDocument(File bdioFile) {
        JsonArray bdioComponents = new JsonParser().parse(new JsonReader(new FileReader(bdioFile))).getAsJsonArray()
        for(JsonElement element : bdioComponents) {
            JsonObject component = element.getAsJsonObject()
            if(component.getAsJsonPrimitive('@type').getAsString().equalsIgnoreCase('project')) {
                BdioPolicyModel bdioPolicyModel = new BdioPolicyModel()
                bdioPolicyModel.name = component.getAsJsonPrimitive('name').getAsString()
                bdioPolicyModel.version = component.getAsJsonPrimitive('revision').getAsString()

                return bdioPolicyModel
            }
        }

        null
    }
}
