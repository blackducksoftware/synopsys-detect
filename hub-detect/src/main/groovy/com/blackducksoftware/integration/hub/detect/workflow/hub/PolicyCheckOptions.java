package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.util.List;

import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType;

public class PolicyCheckOptions {
    private List<PolicySeverityType> severitiesToFailPolicyCheck;

    public PolicyCheckOptions(final List<PolicySeverityType> severitiesToFailPolicyCheck) {
        this.severitiesToFailPolicyCheck = severitiesToFailPolicyCheck;
    }

    public List<PolicySeverityType> getSeveritiesToFailPolicyCheck() {
        return severitiesToFailPolicyCheck;
    }

    public boolean shouldPerformPolicyCheck() {
        return severitiesToFailPolicyCheck.size() > 0;
    }
}
