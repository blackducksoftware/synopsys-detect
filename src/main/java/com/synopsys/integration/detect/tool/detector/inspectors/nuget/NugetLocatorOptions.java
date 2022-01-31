package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.util.List;
import java.util.Optional;

public class NugetLocatorOptions {
    private final List<String> packagesRepoUrl;
    private final String nugetInspectorVersion;

    public NugetLocatorOptions(List<String> packagesRepoUrl, String nugetInspectorVersion) {
        this.packagesRepoUrl = packagesRepoUrl;
        this.nugetInspectorVersion = nugetInspectorVersion;
    }

    public List<String> getPackagesRepoUrl() {
        return packagesRepoUrl;
    }

    public Optional<String> getNugetInspectorVersion() {
        return Optional.ofNullable(nugetInspectorVersion);
    }
}
