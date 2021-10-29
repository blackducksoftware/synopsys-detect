package com.synopsys.integration.detect.workflow.blackduck.policy;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.blackduck.api.core.BlackDuckView;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionComponentPolicyStatusType;

public class ActivePolicyRule extends BlackDuckView {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("status")
    public ProjectVersionComponentPolicyStatusType status;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProjectVersionComponentPolicyStatusType getStatus() {
        return status;
    }
}
