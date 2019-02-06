package com.synopsys.integration.detectable.detectables.gradle.inspector;

import java.util.Optional;

public class GradleInspectorScriptOptions {
    private String excludedProjectNames;
    private String includedProjectNames;
    private String excludedConfigurationNames;
    private String includedConfigurationNames;
    private String gradleInspectorRepositoryUrl;
    private String onlineInspectorVersion;
    private String offlineLibraryPaths;

    public static GradleInspectorScriptOptions forOnlineInspector(String inspectorVersion, String excludedProjectNames, String includedProjectNames, String excludedConfigurationNames, String includedConfigurationNames,
        final String gradleInspectorRepositoryUrl) {
        return new GradleInspectorScriptOptions(excludedProjectNames, includedProjectNames, excludedConfigurationNames, includedConfigurationNames, gradleInspectorRepositoryUrl, inspectorVersion, null);
    }

    public static GradleInspectorScriptOptions forOfflineInspector(String libraryPaths, String excludedProjectNames, String includedProjectNames, String excludedConfigurationNames, String includedConfigurationNames,
        final String gradleInspectorRepositoryUrl) {
        return new GradleInspectorScriptOptions(excludedProjectNames, includedProjectNames, excludedConfigurationNames, includedConfigurationNames, gradleInspectorRepositoryUrl, null, libraryPaths);
    }

    private GradleInspectorScriptOptions(final String excludedProjectNames, final String includedProjectNames, final String excludedConfigurationNames, final String includedConfigurationNames,
        final String gradleInspectorRepositoryUrl, final String onlineInspectorVersion, final String offlineLibraryPaths) {
        this.excludedProjectNames = excludedProjectNames;
        this.includedProjectNames = includedProjectNames;
        this.excludedConfigurationNames = excludedConfigurationNames;
        this.includedConfigurationNames = includedConfigurationNames;
        this.gradleInspectorRepositoryUrl = gradleInspectorRepositoryUrl;
        this.onlineInspectorVersion = onlineInspectorVersion;
        this.offlineLibraryPaths = offlineLibraryPaths;
    }

    public String getGradleInspectorRepositoryUrl() {
        return gradleInspectorRepositoryUrl;
    }

    public String getExcludedProjectNames() {
        return excludedProjectNames;
    }

    public String getIncludedProjectNames() {
        return includedProjectNames;
    }

    public String getExcludedConfigurationNames() {
        return excludedConfigurationNames;
    }

    public String getIncludedConfigurationNames() {
        return includedConfigurationNames;
    }

    public Optional<String> getOnlineInspectorVersion() {
        return Optional.ofNullable(onlineInspectorVersion);
    }

    public Optional<String> getOfflineLibraryPaths() {
        return Optional.ofNullable(offlineLibraryPaths);
    }
}
