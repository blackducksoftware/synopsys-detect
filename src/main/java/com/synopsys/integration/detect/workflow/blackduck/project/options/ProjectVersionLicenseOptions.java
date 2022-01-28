package com.synopsys.integration.detect.workflow.blackduck.project.options;

import java.util.List;

public class ProjectVersionLicenseOptions {
    private List<String> licenseNames;

    public ProjectVersionLicenseOptions(final List<String> licenseNames) {
        this.licenseNames = licenseNames;
    }

    public List<String> getLicenseNames() {
        return licenseNames;
    }
}
