package com.blackducksoftware.integration.hub.detect.bomtool.nuget;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;

public class NugetConfig {

    private final boolean ignoreFailure;
    private final String includedModules;
    private final String excludedModules;
    private final String packageRepoUrl;
    private final String nugetConfigPath;

    public NugetConfig(final DetectConfigWrapper detectConfigWrapper) {
        this.ignoreFailure = detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_NUGET_IGNORE_FAILURE);
        this.excludedModules = detectConfigWrapper.getProperty(DetectProperty.DETECT_NUGET_EXCLUDED_MODULES);
        this.includedModules = detectConfigWrapper.getProperty(DetectProperty.DETECT_NUGET_INCLUDED_MODULES);
        this.packageRepoUrl = detectConfigWrapper.getProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL);
        this.nugetConfigPath = detectConfigWrapper.getProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH);
    }

    public NugetConfig(final boolean ignoreFailure, final String includedModules, final String excludedModules, final String packageRepoUrl, final String nugetConfigPath) {
        this.ignoreFailure = ignoreFailure;
        this.includedModules = includedModules;
        this.excludedModules = excludedModules;
        this.packageRepoUrl = packageRepoUrl;
        this.nugetConfigPath = nugetConfigPath;
    }

    public boolean isIgnoreFailure() {
        return ignoreFailure;
    }

    public String getIncludedModules() {
        return includedModules;
    }

    public String getExcludedModules() {
        return excludedModules;
    }

    public String getPackageRepoUrl() {
        return packageRepoUrl;
    }

    public String getNugetConfigPath() {
        return nugetConfigPath;
    }

}
