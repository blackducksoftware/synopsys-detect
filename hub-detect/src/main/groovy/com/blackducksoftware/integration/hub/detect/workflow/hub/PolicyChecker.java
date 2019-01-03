/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.util.Optional;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType;
import com.synopsys.integration.blackduck.api.generated.enumeration.PolicySummaryStatusType;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.VersionBomPolicyStatusView;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.model.PolicyStatusDescription;
import com.synopsys.integration.exception.IntegrationException;

public class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class);

    private final DetectConfiguration detectConfiguration;

    public PolicyChecker(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    /**
     * For the given DetectProject, find the matching Hub project/version, then all of its code locations, then all of their scan summaries, wait until they are all complete, then get the policy status.
     * @throws IntegrationException
     */
    public Optional<PolicyStatusDescription> getPolicyStatus(final ProjectService projectService, final ProjectVersionView version) throws IntegrationException {
        final Optional<VersionBomPolicyStatusView> versionBomPolicyStatusView = projectService.getPolicyStatusForVersion(version);
        if (!versionBomPolicyStatusView.isPresent()) {
            return Optional.empty();
        }

        final PolicyStatusDescription policyStatusDescription = new PolicyStatusDescription(versionBomPolicyStatusView.get());

        PolicySummaryStatusType statusEnum = PolicySummaryStatusType.NOT_IN_VIOLATION;
        if (policyStatusDescription.getCountInViolation() != null && policyStatusDescription.getCountInViolation().value > 0) {
            statusEnum = PolicySummaryStatusType.IN_VIOLATION;
        } else if (policyStatusDescription.getCountInViolationOverridden() != null && policyStatusDescription.getCountInViolationOverridden().value > 0) {
            statusEnum = PolicySummaryStatusType.IN_VIOLATION_OVERRIDDEN;
        }
        logger.info(String.format("Policy Status: %s", statusEnum.name()));
        return Optional.of(policyStatusDescription);
    }

    public boolean policyViolated(final PolicyStatusDescription policyStatusDescription) {
        final String policyFailOnSeverity = detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, PropertyAuthority.None);
        if (StringUtils.isEmpty(policyFailOnSeverity)) {
            return isAnyPolicyViolated(policyStatusDescription);
        }

        final String[] policySeverityCheck = policyFailOnSeverity.split(",");
        return arePolicySeveritiesViolated(policyStatusDescription, policySeverityCheck);
    }

    private boolean isAnyPolicyViolated(final PolicyStatusDescription policyStatusDescription) {
        final int inViolationCount = policyStatusDescription.getCountOfStatus(PolicySummaryStatusType.IN_VIOLATION);
        return inViolationCount != 0;
    }

    private boolean arePolicySeveritiesViolated(final PolicyStatusDescription policyStatusDescription, final String[] severityCheckList) {
        for (final String policySeverity : severityCheckList) {
            final String formattedPolicySeverity = policySeverity.toUpperCase().trim();
            final PolicySeverityType policySeverityType = EnumUtils.getEnum(PolicySeverityType.class, formattedPolicySeverity);
            if (policySeverityType != null) {
                final int severityCount = policyStatusDescription.getCountOfSeverity(policySeverityType);
                if (severityCount > 0) {
                    return true;
                }
            }
        }

        return false;
    }

}
