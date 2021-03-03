/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.inspector.nuget;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class NugetInspectorOptions {
    private final boolean ignoreFailures;
    private final List<String> excludedModules;
    private final List<String> includedModules;
    private final List<String> packagesRepoUrl;
    private final Path nugetConfigPath;

    public NugetInspectorOptions(boolean ignoreFailures, List<String> excludedModules, List<String> includedModules, List<String> packagesRepoUrl, Path nugetConfigPath) {
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
