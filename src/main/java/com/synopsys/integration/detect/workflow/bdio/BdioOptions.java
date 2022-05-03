package com.synopsys.integration.detect.workflow.bdio;

public class BdioOptions {
    private final String projectCodeLocationSuffix;
    private final String projectCodeLocationPrefix;

    public BdioOptions(String projectCodeLocationPrefix, String projectCodeLocationSuffix) {
        this.projectCodeLocationSuffix = projectCodeLocationSuffix;
        this.projectCodeLocationPrefix = projectCodeLocationPrefix;
    }

    public String getProjectCodeLocationSuffix() {
        return projectCodeLocationSuffix;
    }

    public String getProjectCodeLocationPrefix() {
        return projectCodeLocationPrefix;
    }

}
