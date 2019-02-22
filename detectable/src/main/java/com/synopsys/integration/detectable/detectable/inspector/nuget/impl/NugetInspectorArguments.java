package com.synopsys.integration.detectable.detectable.inspector.nuget.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;

public class NugetInspectorArguments {
    private static final Logger logger = LoggerFactory.getLogger(NugetInspectorArguments.class);

    //At the time of righting, both inspectors (exe and dotnet) use the same arguments so a shared static method is provided.
    //If they diverge the options object protects the argument conversion so each inspector can convert as they see fit.
    public static List<String> fromInspectorOptions(NugetInspectorOptions nugetInspectorOptions) throws IOException {
            final List<String> options = new ArrayList<>(Arrays.asList(
                "--target_path=" + nugetInspectorOptions.getTargetDirectory().getCanonicalPath(),
                "--output_directory=" + nugetInspectorOptions.getOutputDirectory().getCanonicalPath(),
                "--ignore_failure=" + nugetInspectorOptions.isIgnoreFailures()));

            final String nugetExcludedModules = nugetInspectorOptions.getExcludedModules();
            if (StringUtils.isNotBlank(nugetExcludedModules)) {
                options.add("--excluded_modules=" + nugetExcludedModules);
            }
            final String nugetIncludedModules = nugetInspectorOptions.getIncludedModules();
            if (StringUtils.isNotBlank(nugetIncludedModules)) {
                options.add("--included_modules=" + nugetIncludedModules);
            }
            final String[] nugetPackagesRepo = nugetInspectorOptions.getPackagesRepoUrl();
            if (nugetPackagesRepo.length > 0) {
                final String packagesRepos = Arrays.asList(nugetPackagesRepo).stream().collect(Collectors.joining(","));
                options.add("--packages_repo_url=" + packagesRepos);
            }
            final String nugetConfigPath = nugetInspectorOptions.getNugetConfigPath();
            if (StringUtils.isNotBlank(nugetConfigPath)) {
                options.add("--nuget_config_path=" + nugetConfigPath);
            }
            if (logger.isTraceEnabled()) {
                options.add("-v");
            }

            return options;
    }
}
