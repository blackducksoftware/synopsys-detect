package com.synopsys.integration.detect.tool.detector.inspectors;

public class NugetInstallerOptions {
    private final String[] packagesRepoUrl;
    private final String nugetInspectorName;
    private final String nugetInspectorVersion;

    public NugetInstallerOptions(final String[] packagesRepoUrl, final String nugetInspectorName, final String nugetInspectorVersion) {
        this.packagesRepoUrl = packagesRepoUrl;
        this.nugetInspectorName = nugetInspectorName;
        this.nugetInspectorVersion = nugetInspectorVersion;
    }

    public String[] getPackagesRepoUrl() {
        return packagesRepoUrl;
    }

    public String getNugetInspectorName() {
        return nugetInspectorName;
    }

    public String getNugetInspectorVersion() {
        return nugetInspectorVersion;
    }
}
