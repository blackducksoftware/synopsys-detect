package com.synopsys.integration.detect.workflow.blackduck.project.options;

public class FindCloneOptions {
    private final String cloneVersionName;
    private final Boolean cloneLatestProjectVersion;

    public FindCloneOptions(String cloneVersionName, Boolean cloneLatestProjectVersion) {
        this.cloneVersionName = cloneVersionName;
        this.cloneLatestProjectVersion = cloneLatestProjectVersion;
    }

    public String getCloneVersionName() {
        return cloneVersionName;
    }

    public Boolean getCloneLatestProjectVersion() {
        return cloneLatestProjectVersion;
    }
}
