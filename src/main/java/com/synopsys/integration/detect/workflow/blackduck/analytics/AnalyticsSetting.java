package com.synopsys.integration.detect.workflow.blackduck.analytics;

import com.synopsys.integration.blackduck.api.core.BlackDuckResponse;

public class AnalyticsSetting extends BlackDuckResponse {
    private final String name;
    private final boolean value;

    public AnalyticsSetting(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return value;
    }

}
