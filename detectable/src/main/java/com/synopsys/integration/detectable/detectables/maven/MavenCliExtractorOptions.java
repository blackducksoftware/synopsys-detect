package com.synopsys.integration.detectable.detectables.maven;

public class MavenCliExtractorOptions {
    private final String mavenBuildCommand;
    private final String mavenScope;
    private final String mavenExcludedModules;
    private final String mavenIncludedModules;

    public MavenCliExtractorOptions(final String mavenBuildCommand, final String mavenScope, final String mavenExcludedModules, final String mavenIncludedModules) {
        this.mavenBuildCommand = mavenBuildCommand;
        this.mavenScope = mavenScope;
        this.mavenExcludedModules = mavenExcludedModules;
        this.mavenIncludedModules = mavenIncludedModules;
    }

    public String getMavenBuildCommand() {
        return mavenBuildCommand;
    }

    public String getMavenScope() {
        return mavenScope;
    }

    public String getMavenExcludedModules() {
        return mavenExcludedModules;
    }

    public String getMavenIncludedModules() {
        return mavenIncludedModules;
    }
}
