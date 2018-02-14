/*
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import com.blackducksoftware.integration.hub.api.enumeration.PolicySeverityType
import com.blackducksoftware.integration.hub.api.generated.enumeration.PolicyStatusApprovalStatusType
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView
import com.blackducksoftware.integration.hub.api.generated.view.VersionBomPolicyStatusView
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.service.PolicyStatusService
import com.blackducksoftware.integration.hub.service.model.PolicyStatusDescription

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
    public PolicyStatusDescription getPolicyStatus(PolicyStatusService policyStatusService, ProjectVersionView version) {
        VersionBomPolicyStatusView versionBomPolicyStatusView = policyStatusService.getPolicyStatusForVersion(version)
        PolicyStatusDescription policyStatusDescription = new PolicyStatusDescription(versionBomPolicyStatusView)

        PolicyStatusApprovalStatusType statusEnum = PolicyStatusApprovalStatusType.NOT_IN_VIOLATION
        if (policyStatusDescription.getCountInViolation()?.value > 0) {
            statusEnum = PolicyStatusApprovalStatusType.IN_VIOLATION
        } else if (policyStatusDescription.getCountInViolationOverridden()?.value > 0) {
            statusEnum = PolicyStatusApprovalStatusType.IN_VIOLATION_OVERRIDDEN
        }
        logger.info("Policy Status: ${statusEnum.name()}")
        policyStatusDescription
    }

    public boolean policyViolated(PolicyStatusDescription policyStatusDescription) {
        String policyFailOnSeverity = detectConfiguration.getPolicyCheckFailOnSeverities()
        if (StringUtils.isEmpty(policyFailOnSeverity)) {
            return isAnyPolicyViolated(policyStatusDescription)
        }

        String[] policySeverityCheck = policyFailOnSeverity.split(',')
        return arePolicySeveritiesViolated(policyStatusDescription, policySeverityCheck)
    }

    private boolean isAnyPolicyViolated(PolicyStatusDescription policyStatusDescription) {
        int inViolationCount = policyStatusDescription.getCountOfStatus(PolicyStatusApprovalStatusType.IN_VIOLATION)
        return inViolationCount != 0
    }

    private boolean arePolicySeveritiesViolated(PolicyStatusDescription policyStatusDescription, String[] severityCheckList) {
        for (String policySeverity : severityCheckList) {
            String formattedPolicySeverity = policySeverity.toUpperCase().trim()
            PolicySeverityType policySeverityType = EnumUtils.getEnum(PolicySeverityType.class, formattedPolicySeverity)
            if (policySeverityType != null) {
                int severityCount = policyStatusDescription.getCountOfSeverity(policySeverityType)
                if (severityCount > 0) {
                    return true
                }
            }
        }

        return false
    }
}
