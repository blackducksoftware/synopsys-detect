/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.maven.cli;

import java.util.List;
import java.util.Optional;

public class MavenCliExtractorOptions {
    private final List<String> mavenBuildCommandArguments;
    private final List<String> mavenExcludedScopes;
    private final List<String> mavenIncludedScopes;
    private final List<String> mavenExcludedModules;
    private final List<String> mavenIncludedModules;

    public MavenCliExtractorOptions(List<String> mavenBuildCommandArguments, List<String> mavenExcludedScopes, List<String> mavenIncludedScopes, List<String> mavenExcludedModules, List<String> mavenIncludedModules) {
        this.mavenBuildCommandArguments = mavenBuildCommandArguments;
        this.mavenExcludedScopes = mavenExcludedScopes;
        this.mavenIncludedScopes = mavenIncludedScopes;
        this.mavenExcludedModules = mavenExcludedModules;
        this.mavenIncludedModules = mavenIncludedModules;
    }

    public Optional<List<String>> getMavenBuildCommandArguments() {
        return Optional.ofNullable(mavenBuildCommandArguments);
    }

    public List<String> getMavenExcludedScopes() {
        return mavenExcludedScopes;
    }

    public List<String> getMavenIncludedScopes() {
        return mavenIncludedScopes;
    }

    public List<String> getMavenExcludedModules() {
        return mavenExcludedModules;
    }

    public List<String> getMavenIncludedModules() {
        return mavenIncludedModules;
    }
}
