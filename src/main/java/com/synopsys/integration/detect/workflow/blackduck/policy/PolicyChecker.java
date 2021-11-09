/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.policy;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.enumeration.PolicyRuleSeverityType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionComponentPolicyStatusType;
import com.synopsys.integration.blackduck.api.generated.view.ComponentPolicyRulesView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.dataservice.ProjectBomService;
import com.synopsys.integration.blackduck.service.model.PolicyStatusDescription;
import com.synopsys.integration.common.util.Bdo;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.exception.IntegrationException;

public class PolicyChecker {
    private final Logger logger = LoggerFactory.getLogger(PolicyChecker.class);

    private final ExitCodePublisher exitCodePublisher;
    private final BlackDuckApiClient blackDuckService;
    private final ProjectBomService projectBomService;

    public PolicyChecker(ExitCodePublisher exitCodePublisher, BlackDuckApiClient blackDuckService, ProjectBomService projectBomService) {
        this.exitCodePublisher = exitCodePublisher;
        this.blackDuckService = blackDuckService;
        this.projectBomService = projectBomService;
    }

    public void checkPolicy(List<PolicyRuleSeverityType> policySeverities, ProjectVersionView projectVersionView) throws IntegrationException {
        Optional<PolicyStatusDescription> policyStatusDescription = fetchPolicyStatusDescription(projectVersionView);

        if (policyStatusDescription.isPresent()) {
            logger.info(policyStatusDescription.get().getPolicyStatusMessage());

            if (arePolicySeveritiesViolated(policyStatusDescription.get(), policySeverities)) {
                fetchAndLogPolicyViolations(projectVersionView);
                exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_POLICY_VIOLATION, policyStatusDescription.get().getPolicyStatusMessage());
            }
        } else {
            String availableLinks = StringUtils.join(projectVersionView.getAvailableLinks(), ", ");
            logger.warn(String.format("It is not possible to check the policy status for this project/version. The policy-status link must be present. The available links are: %s", availableLinks));
        }
    }

    public Optional<PolicyStatusDescription> fetchPolicyStatusDescription(ProjectVersionView version) throws IntegrationException {
        return Bdo.of(projectBomService.getPolicyStatusForVersion(version))
            .peek(policyStatus -> logger.info(String.format("Policy Status: %s", policyStatus.getOverallStatus().name())))
            .map(PolicyStatusDescription::new)
            .toOptional();
    }

    public void fetchAndLogPolicyViolations(ProjectVersionView projectVersionView) throws IntegrationException {
        logger.info("Searching BOM for components in violation of policy rules.");

        List<ProjectVersionComponentVersionView> bomComponents = projectBomService.getComponentsForProjectVersion(projectVersionView);
        for (ProjectVersionComponentVersionView projectVersionComponentView : bomComponents) {
            if (projectVersionComponentView.getPolicyStatus().equals(ProjectVersionComponentPolicyStatusType.NOT_IN_VIOLATION)) {
                continue;
            }

            for (ComponentPolicyRulesView componentPolicyRulesView : blackDuckService.getAllResponses(projectVersionComponentView.metaPolicyRulesLink())) {
                String componentId = projectVersionComponentView.getComponentName();
                if (StringUtils.isNotBlank(projectVersionComponentView.getComponentVersionName())) {
                    componentId += ":" + projectVersionComponentView.getComponentVersionName();
                }

                String policyRuleComponentVersionSuffix = ".";
                if (StringUtils.isNotBlank(projectVersionComponentView.getComponentVersion())) {
                    policyRuleComponentVersionSuffix = String.format(" (%s).", projectVersionComponentView.getComponentVersion());
                }
                logger.info(String.format("Policy rule \"%s\" was violated by component \"%s\"%s",
                    componentPolicyRulesView.getName(),
                    componentId,
                    policyRuleComponentVersionSuffix
                ));

                String policyRuleSuffix = ".";
                if (StringUtils.isNotBlank(componentPolicyRulesView.getDescription())) {
                    policyRuleSuffix = String.format(" with description: %s", componentPolicyRulesView.getDescription());
                }

                logger.info(String.format("Policy rule \"%s\" has a severity type of %s%s",
                    componentPolicyRulesView.getName(),
                    componentPolicyRulesView.getSeverity().prettyPrint(),
                    policyRuleSuffix
                ));

            }
        }
    }

    private boolean arePolicySeveritiesViolated(PolicyStatusDescription policyStatusDescription, List<PolicyRuleSeverityType> policySeverities) {
        return policySeverities.stream()
            .map(policyStatusDescription::getCountOfSeverity)
            .anyMatch(severityCount -> severityCount > 0);
    }

}
