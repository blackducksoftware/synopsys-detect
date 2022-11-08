package com.synopsys.integration.detectable.detectables.nuget;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class NugetInspectorOptions {
    private final boolean ignoreFailures;
    private final List<String> excludedModules;
    private final List<String> includedModules;
    private final List<String> packagesRepoUrl;
    private final Path nugetConfigPath;

    public NugetInspectorOptions(boolean ignoreFailures, List<String> excludedModules, List<String> includedModules, List<String> packagesRepoUrl, @Nullable Path nugetConfigPath) {
        this.ignoreFailures = ignoreFailures;
        this.excludedModules = excludedModules;
        this.includedModules = includedModules;
        this.packagesRepoUrl = packagesRepoUrl;
        this.nugetConfigPath = nugetConfigPath;
    }

    public boolean isIgnoreFailures() {
        return ignoreFailures;
    }

    public List<String> getExcludedModules() {
        return excludedModules;
    }

    public List<String> getIncludedModules() {
        return includedModules;
    }

    public List<String> getPackagesRepoUrl() {
        return packagesRepoUrl;
    }

    public Optional<Path> getNugetConfigPath() {
        return Optional.ofNullable(nugetConfigPath);
    }
}
