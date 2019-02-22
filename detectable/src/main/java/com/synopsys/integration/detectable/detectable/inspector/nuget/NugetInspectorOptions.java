package com.synopsys.integration.detectable.detectable.inspector.nuget;

import java.io.File;

public class NugetInspectorOptions {

    private final File targetDirectory;
    private final File outputDirectory;
    private final boolean ignoreFailures;
    private final String excludedModules;
    private final String includedModules;
    private final String[] packagesRepoUrl;
    private final String nugetConfigPath;

    public NugetInspectorOptions(final File targetDirectory, final File outputDirectory, final boolean ignoreFailures, final String excludedModules, final String includedModules, final String packagesRepoUrl[],
        final String nugetConfigPath) {
        this.targetDirectory = targetDirectory;
        this.outputDirectory = outputDirectory;
        this.ignoreFailures = ignoreFailures;
        this.excludedModules = excludedModules;
        this.includedModules = includedModules;
        this.packagesRepoUrl = packagesRepoUrl;
        this.nugetConfigPath = nugetConfigPath;
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public boolean isIgnoreFailures() {
        return ignoreFailures;
    }

    public String getExcludedModules() {
        return excludedModules;
    }

    public String getIncludedModules() {
        return includedModules;
    }

    public String[] getPackagesRepoUrl() {
        return packagesRepoUrl;
    }

    public String getNugetConfigPath() {
        return nugetConfigPath;
    }
}
