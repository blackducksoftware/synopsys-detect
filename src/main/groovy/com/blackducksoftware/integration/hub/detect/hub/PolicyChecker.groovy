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

import org.apache.commons.lang3.EnumUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDescription
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.exception.DetectException
import com.blackducksoftware.integration.hub.model.enumeration.PolicySeverityEnum
import com.blackducksoftware.integration.hub.model.enumeration.VersionBomPolicyStatusOverallStatusEnum
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView
import com.blackducksoftware.integration.hub.model.view.VersionBomPolicyStatusView

import groovy.transform.TypeChecked

@Component
@TypeChecked
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
    public PolicyStatusDescription getPolicyStatus(PolicyStatusDataService policyStatusDataService, ProjectVersionView version) throws DetectException {
        VersionBomPolicyStatusView versionBomPolicyStatusView = policyStatusDataService.getPolicyStatusForVersion(version)
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

    public boolean policyViolated(PolicyStatusDescription policyStatusDescription) {
        String policyFailOnSeverity = detectConfiguration.getPolicyFailOnSeverity()
        if (StringUtils.isEmpty(policyFailOnSeverity)) {
            return checkIfAnyPolicyViolated(policyStatusDescription)
        }

        String[] policySeverityCheck = policyFailOnSeverity.split(',')
        return checkForPolicySeverityViolation(policyStatusDescription, policySeverityCheck)
    }

    private boolean checkIfAnyPolicyViolated(PolicyStatusDescription policyStatusDescription) {
        int inViolationCount = policyStatusDescription.getCountOfStatus(VersionBomPolicyStatusOverallStatusEnum.IN_VIOLATION)
        return inViolationCount != 0
    }

    private boolean checkForPolicySeverityViolation(PolicyStatusDescription policyStatusDescription, String[] severityCheckList) {
        for (String policySeverity : severityCheckList) {
            String formattedPolicySeverity = policySeverity.toUpperCase().trim()
            PolicySeverityEnum policySeverityEnum = EnumUtils.getEnum(PolicySeverityEnum.class, formattedPolicySeverity)
            if (policySeverityEnum != null) {
                int severityCount = policyStatusDescription.getCountOfSeverity(policySeverityEnum)
                if (severityCount > 0) {
                    return true
                }
            }
        }

        return false
    }
}
