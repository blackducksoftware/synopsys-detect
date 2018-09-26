package com.blackducksoftware.integration.hub.detect.bomtool.nuget.inspector;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class NugetInspectorArgumentBuilder {
    private String targetPath;
    private String outputDirectory;
    private boolean ignoreFailure;
    private String includedModules;
    private String excludedModules;
    private String packagesRepoUrl;
    private String nugetConfigPath;
    private boolean verbose;

    public NugetInspectorArgumentBuilder setTargetPath(final String targetPath) {
        this.targetPath = targetPath;
        return this;
    }

    public NugetInspectorArgumentBuilder setOutputDirectory(final String outputDirectory) {
        this.outputDirectory = outputDirectory;
        return this;
    }

    public NugetInspectorArgumentBuilder setIgnoreFailure(final boolean ignoreFailure) {
        this.ignoreFailure = ignoreFailure;
        return this;
    }

    public NugetInspectorArgumentBuilder setIncludedModules(final String includedModules) {
        this.includedModules = includedModules;
        return this;
    }

    public NugetInspectorArgumentBuilder setExcludedModules(final String excludedModules) {
        this.excludedModules = excludedModules;
        return this;
    }

    public NugetInspectorArgumentBuilder setPackagesRepoUrl(final String packagesRepoUrl) {
        this.packagesRepoUrl = packagesRepoUrl;
        return this;
    }

    public NugetInspectorArgumentBuilder setNugetConfigPath(final String nugetConfigPath) {
        this.nugetConfigPath = nugetConfigPath;
        return this;
    }

    public NugetInspectorArgumentBuilder setVerbose(final boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public List<String> build() {
        final List<String> arguments = new ArrayList<>();
        add(arguments, "target_path", targetPath);
        add(arguments, "output_directory", outputDirectory);
        add(arguments, "ignore_failure", BooleanUtils.toStringTrueFalse(ignoreFailure));

        addIfExists(arguments, "excluded_modules", excludedModules);
        addIfExists(arguments, "included_modules", includedModules);
        addIfExists(arguments, "packages_repo_url", packagesRepoUrl);
        addIfExists(arguments, "nuget_config_path", nugetConfigPath);

        if (verbose) {
            arguments.add("-v");
        }

        return arguments;
    }

    private void addIfExists(List<String> arguments, String arg, String value) {
        if (StringUtils.isNotBlank(value)) {
            arguments.add(arg + value);
        }
    }

    private void add(List<String> arguments, String arg, String value) {
        arguments.add("--" + arg + "=" + value);
    }
}