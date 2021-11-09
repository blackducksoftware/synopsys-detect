package com.synopsys.integration.detect.workflow.blackduck;

import com.synopsys.integration.blackduck.api.core.response.LinkMultipleResponses;
import com.synopsys.integration.blackduck.api.core.response.UrlMultipleResponses;
import com.synopsys.integration.blackduck.api.generated.view.ComponentPolicyRulesView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentVersionView;
import com.synopsys.integration.rest.HttpUrl;

public class PolicyRulesUtil { //remove when ProjectVersionComponentVersionView is fixed
    public static final String POLICY_RULES_LINK = "policy-rules";
    public static final LinkMultipleResponses<ComponentPolicyRulesView> POLICY_RULES_LINK_RESPONSE = new LinkMultipleResponses<ComponentPolicyRulesView>(POLICY_RULES_LINK, ComponentPolicyRulesView.class);

    public static UrlMultipleResponses<ComponentPolicyRulesView> metaPolicyRulesLink(ProjectVersionComponentVersionView projectVersionComponentVersionView) {
        HttpUrl url = projectVersionComponentVersionView.getFirstLink(POLICY_RULES_LINK_RESPONSE.getLink());
        return new UrlMultipleResponses<>(url, POLICY_RULES_LINK_RESPONSE.getResponseClass());
    }
}
