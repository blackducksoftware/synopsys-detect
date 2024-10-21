package com.blackduck.integration.detect.workflow.blackduck.policy;

import com.blackduck.integration.blackduck.api.generated.view.ComponentPolicyRulesView;
import com.blackduck.integration.blackduck.api.generated.view.ProjectVersionComponentVersionView;

public class PolicyViolationInfo {
    private final ProjectVersionComponentVersionView projectVersionComponentVersionView;
    private final ComponentPolicyRulesView componentPolicyRulesView;

    public ProjectVersionComponentVersionView getProjectVersionComponentVersionView() {
        return projectVersionComponentVersionView;
    }

    public ComponentPolicyRulesView getComponentPolicyRulesView() {
        return componentPolicyRulesView;
    }

    public PolicyViolationInfo(
        ProjectVersionComponentVersionView projectVersionComponentVersionView,
        ComponentPolicyRulesView componentPolicyRulesView
    ) {
        this.projectVersionComponentVersionView = projectVersionComponentVersionView;
        this.componentPolicyRulesView = componentPolicyRulesView;
    }
}
