package com.synopsys.integration.detectable.detectables.maven.cli;

import java.util.List;
import java.util.Optional;

public class MavenCliExtractorOptions {
    private final String mavenBuildCommand;
    private final List<String> mavenExcludedScopes;
    private final List<String> mavenIncludedScopes;
    private final List<String> mavenExcludedModules;
    private final List<String> mavenIncludedModules;

    public MavenCliExtractorOptions(
        String mavenBuildCommand,
        List<String> mavenExcludedScopes,
        List<String> mavenIncludedScopes,
        List<String> mavenExcludedModules,
        List<String> mavenIncludedModules
    ) {
        this.mavenBuildCommand = mavenBuildCommand;
        this.mavenExcludedScopes = mavenExcludedScopes;
        this.mavenIncludedScopes = mavenIncludedScopes;
        this.mavenExcludedModules = mavenExcludedModules;
        this.mavenIncludedModules = mavenIncludedModules;
    }

    public Optional<String> getMavenBuildCommand() {
        return Optional.ofNullable(mavenBuildCommand);
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
