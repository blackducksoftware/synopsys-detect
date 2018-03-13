package com.blackducksoftware.integration.hub.detect.hub;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.enumeration.PolicySeverityType;
import com.blackducksoftware.integration.hub.api.generated.enumeration.PolicyStatusApprovalStatusType;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.api.generated.view.VersionBomPolicyStatusView;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.service.ProjectService;
import com.blackducksoftware.integration.hub.service.model.PolicyStatusDescription;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class);

    @Autowired
    private DetectConfiguration detectConfiguration;

    /**
     * For the given DetectProject, find the matching Hub project/version, then all of its code locations, then all of their scan summaries, wait until they are all complete, then get the policy status.
     *
     * @throws IntegrationException
     */
    public PolicyStatusDescription getPolicyStatus(final ProjectService projectService, final ProjectVersionView version) throws IntegrationException {
        final VersionBomPolicyStatusView versionBomPolicyStatusView = projectService.getPolicyStatusForVersion(version);
        final PolicyStatusDescription policyStatusDescription = new PolicyStatusDescription(versionBomPolicyStatusView);

        PolicyStatusApprovalStatusType statusEnum = PolicyStatusApprovalStatusType.NOT_IN_VIOLATION;
        if (policyStatusDescription.getCountInViolation() != null && policyStatusDescription.getCountInViolation().value > 0) {
            statusEnum = PolicyStatusApprovalStatusType.IN_VIOLATION;
        } else if (policyStatusDescription.getCountInViolationOverridden() != null && policyStatusDescription.getCountInViolationOverridden().value > 0) {
            statusEnum = PolicyStatusApprovalStatusType.IN_VIOLATION_OVERRIDDEN;
        }
        logger.info(String.format("Policy Status: %s", statusEnum.name()));
        return policyStatusDescription;
    }

    public boolean policyViolated(final PolicyStatusDescription policyStatusDescription) {
        final String policyFailOnSeverity = detectConfiguration.getPolicyCheckFailOnSeverities();
        if (StringUtils.isEmpty(policyFailOnSeverity)) {
            return isAnyPolicyViolated(policyStatusDescription);
        }

        final String[] policySeverityCheck = policyFailOnSeverity.split(",");
        return arePolicySeveritiesViolated(policyStatusDescription, policySeverityCheck);
    }

    private boolean isAnyPolicyViolated(final PolicyStatusDescription policyStatusDescription) {
        final int inViolationCount = policyStatusDescription.getCountOfStatus(PolicyStatusApprovalStatusType.IN_VIOLATION);
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
