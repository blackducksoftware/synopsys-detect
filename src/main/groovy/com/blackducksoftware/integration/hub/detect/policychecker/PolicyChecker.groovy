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
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDescription
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.model.view.VersionBomPolicyStatusView

class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class)

    private ScanStatusDataService scanStatusDataService
    private PolicyStatusDataService policyStatusDataService

    public PolicyChecker(ScanStatusDataService scanStatusDataService, PolicyStatusDataService policyStatusDataService) {
        this.scanStatusDataService = scanStatusDataService
        this.policyStatusDataService = policyStatusDataService
    }

    public String getPolicyStatusMessage(String projectName, String projectVersionName) {
        scanStatusDataService.assertBomImportScanStartedThenFinished(projectName, projectVersionName)
        VersionBomPolicyStatusView versionBomPolicyStatusView = policyStatusDataService.getPolicyStatusForProjectAndVersion(projectName, projectVersionName)
        PolicyStatusDescription policyStatusDescription = new PolicyStatusDescription(versionBomPolicyStatusView)

        policyStatusDescription.policyStatusMessage
    }
}
