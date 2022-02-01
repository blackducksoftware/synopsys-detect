package com.synopsys.integration.detect.workflow.blackduck.project.options;

public class ProjectVersionLicenseOptions {
    private final String licenseName;

    public ProjectVersionLicenseOptions(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getLicenseName() {
        return licenseName;
    }
}
