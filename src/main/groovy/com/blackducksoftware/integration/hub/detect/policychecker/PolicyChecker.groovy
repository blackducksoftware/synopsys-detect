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
package com.blackducksoftware.integration.hub.detect.policychecker

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.model.enumeration.VersionBomPolicyStatusOverallStatusEnum
import com.blackducksoftware.integration.hub.model.view.VersionBomPolicyStatusView
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader

class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class)

    private ScanStatusDataService scanStatusDataService
    private PolicyStatusDataService policyStatusDataService

    public PolicyChecker(ScanStatusDataService scanStatusDataService, PolicyStatusDataService policyStatusDataService) {
        this.scanStatusDataService = scanStatusDataService
        this.policyStatusDataService = policyStatusDataService
    }

    public VersionBomPolicyStatusOverallStatusEnum checkForPolicyViolations(BdioPolicy bdioPolicyModel) {
        scanStatusDataService.assertBomImportScanStartedThenFinished(bdioPolicyModel.name, bdioPolicyModel.version)
        VersionBomPolicyStatusView policyStatus = policyStatusDataService.getPolicyStatusForProjectAndVersion(bdioPolicyModel.name, bdioPolicyModel.version)

        policyStatus.overallStatus
    }

    public VersionBomPolicyStatusOverallStatusEnum checkForPolicyViolations(File bdioFile) {
        checkForPolicyViolations(convertFromJsonToSimpleBdioDocument(bdioFile))
    }

    private BdioPolicy convertFromJsonToSimpleBdioDocument(File bdioFile) {
        JsonArray bdioComponents = new JsonParser().parse(new JsonReader(new FileReader(bdioFile))).getAsJsonArray()
        for(JsonElement element : bdioComponents) {
            JsonObject component = element.getAsJsonObject()
            if(component.getAsJsonPrimitive('@type').getAsString().equalsIgnoreCase('project')) {
                BdioPolicy bdioPolicyModel = new BdioPolicy()
                bdioPolicyModel.name = component.getAsJsonPrimitive('name').getAsString()
                bdioPolicyModel.version = component.getAsJsonPrimitive('revision').getAsString()

                return bdioPolicyModel
            }
        }

        null
    }
}
