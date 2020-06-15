package com.synopsys.integration.detect.workflow.blackduck.analytics;

import com.google.gson.annotations.SerializedName;

public class AnalyticsSetting {
    @SerializedName("name")
    private final String name;
    @SerializedName("value")
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
